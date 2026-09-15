"""Run the recorded console cases and live save-file checks, stopping at the first failure."""

from datetime import datetime
import json
from pathlib import Path
from queue import Queue
import re
import subprocess
from tempfile import TemporaryDirectory
from threading import Thread


ROOT = Path(__file__).resolve().parents[1]
PLAN = ROOT / 'test/ui-test-plan.md'


def run_case(inputs, expected, snapshots, directory):
    """Capture a fresh run; inspect storage after each response when checkpoints exist."""
    process = subprocess.Popen(
        ['java', '-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8',
         '-cp', str(ROOT / 'out'), 'proton.Proton'], cwd=directory,
        stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
        text=True, encoding='utf-8',
    )
    if snapshots is None:
        try:
            stdout, stderr = process.communicate(inputs, timeout=15)
            return stdout, stderr, process.returncode, ''
        except subprocess.TimeoutExpired:
            process.kill()
            stdout, stderr = process.communicate()
            return stdout, stderr, 'TIMEOUT', ''

    lines = Queue()
    captured = []
    checks = []

    def read_stdout():
        try:
            for line in process.stdout:
                lines.put(line)
        except UnicodeError as error:
            checks.append(f'FAIL: Console decoding: {error}')
        finally:
            lines.put(None)

    reader = Thread(target=read_stdout, daemon=True)
    reader.start()

    def read_frame():
        separators = 0
        while separators < 2:
            line = lines.get(timeout=15)
            if line is None:
                raise AssertionError('Process ended before the complete response.')
            captured.append(line)
            if line == '_' * 60 + '\n':
                separators += 1

    try:
        read_frame()
        for command, snapshot in zip(inputs.splitlines(), snapshots, strict=True):
            process.stdin.write(command + '\n')
            process.stdin.flush()
            read_frame()
            if not expected.startswith(''.join(captured)):
                raise AssertionError('Console response mismatch.')
            save_file = Path(directory) / 'data/proton.txt'
            actual = save_file.read_text(encoding='utf-8') if save_file.exists() else None
            if actual != snapshot:
                raise AssertionError(f'{command}: expected save {snapshot!r}, got {actual!r}')
            checks.append(f'{command}: PASS; save file = {json.dumps(actual, ensure_ascii=False)}')
        process.stdin.close()
        process.wait(timeout=15)
    except Exception as error:
        checks.append(f'FAIL: {type(error).__name__}: {error}')
        process.kill()
        process.wait()
    reader.join(timeout=15)
    while not lines.empty():
        line = lines.get_nowait()
        if line is not None:
            captured.append(line)
    stderr = process.stderr.read()
    process.stdin.close()
    process.stdout.close()
    process.stderr.close()
    return ''.join(captured), stderr, process.returncode, '\n'.join(checks)


def main():
    base = PLAN.read_text(encoding='utf-8').split('## Latest test session')[0].rstrip() + '\n\n'
    version = subprocess.run(['java', '-version'], capture_output=True, text=True)
    build = subprocess.run(
        ['javac', '--release', '25', '-d', str(ROOT / 'out'),
         *map(str, sorted((ROOT / 'src/main/java').rglob('*.java')))],
        capture_output=True, text=True,
    )
    if build.returncode or not re.search(r'version "25[.\"]', version.stderr):
        PLAN.write_text(base + '## Latest test session\n\nBUILD FAILED\n\n```text\n'
                        + version.stderr + build.stdout + build.stderr + '```\n', encoding='utf-8')
        return 1
    records = []
    failed = False
    (ROOT / '_temp').mkdir(exist_ok=True)
    for name, body in re.findall(r'### (UI-[^\n]+)\n(.*?)(?=\n### |\Z)', base, re.S):
        if failed:
            records.append(f'### {name}\n\nNOT RUN\n')
            continue
        inputs, expected = re.findall(r'```text\n(.*?)```', body, re.S)[:2]
        checkpoint = re.search(r'```json\n(.*?)```', body, re.S)
        configuration = json.loads(checkpoint[1]) if checkpoint else None
        options = configuration if isinstance(configuration, dict) else {}
        snapshots = options.get('snapshots') if options else configuration
        restart_record = ''
        with TemporaryDirectory(prefix='ui-', dir=ROOT / '_temp') as directory:
            save_file = Path(directory) / 'data/proton.txt'
            if 'initial_save' in options:
                save_file.parent.mkdir()
                save_file.write_text(options['initial_save'], encoding='utf-8')
            actual, stderr, code, checks = run_case(inputs, expected, snapshots, directory)
            if 'unchanged_save' in options:
                if save_file.read_text(encoding='utf-8') != options['unchanged_save']:
                    checks += '\nFAIL: Startup changed the save file.'
                else:
                    checks += '\nStartup preserved the save file: PASS'
            if (options.get('restart_expected') and actual == expected
                    and not stderr and code == 0 and 'FAIL:' not in checks):
                restart_input = 'list\nbye\n'
                output, errors, result, _ = run_case(
                    restart_input, options['restart_expected'], None, directory)
                restart_record = (f'\nRestart input:\n\n```text\n{restart_input}```\n\n'
                                  f'Restart stdout:\n\n```text\n{output}```\n\n'
                                  f'Restart stderr:\n\n```text\n{errors}```\n\nRestart exit code: {result}\n')
                if output != options['restart_expected'] or errors or result != 0:
                    checks += '\nFAIL: Restart output mismatch.'
                else:
                    checks += '\nRestart restored the saved list: PASS'
        failed = actual != expected or bool(stderr) or code != 0 or 'FAIL:' in checks
        status = 'FAIL' if failed else 'PASS'
        print(name.split(':')[0], status)
        record = (f'### {name}\n\n{status}; exit code: {code}\n\nInput:\n\n```text\n{inputs}```\n\n'
                  f'Actual stdout:\n\n```text\n{actual}```\n\nStderr:\n\n```text\n{stderr}```\n')
        if checks:
            record += f'\nSave checks:\n\n```text\n{checks}\n```\n'
        record += restart_record
        if failed:
            record += f'\nExpected output:\n\n```text\n{expected}```\n'
        records.append(record)
    session = (f'## Latest test session\n\nTimestamp: {datetime.now().astimezone().isoformat()}\n\n'
               f'Result: {"FAIL" if failed else "PASS"}\n\nJava: {version.stderr.splitlines()[0]}\n\n'
               + '\n'.join(records))
    PLAN.write_text(base + session, encoding='utf-8')
    (ROOT / '_temp/ui-test-session.md').write_text(session, encoding='utf-8')
    return int(failed)


if __name__ == '__main__':
    raise SystemExit(main())
