"""Start the dev client under Xvfb, then stop it after resource loading succeeds."""
import os
from pathlib import Path
import re
import signal
import subprocess
import time

log = Path('run/logs/latest.log')
old_mtime = log.stat().st_mtime_ns if log.exists() else None
command = ['xvfb-run', '-a', '-s', '-screen 0 1280x720x24 +extension GLX +render -noreset',
           'bash', './gradlew', 'runClient', '--no-daemon', '--console=plain', '-PclayGameplaySmoke=true']
process = subprocess.Popen(command, start_new_session=True)
deadline = time.monotonic() + 240
ready_since = None
success = False
try:
    while time.monotonic() < deadline:
        if log.exists() and log.stat().st_mtime_ns != old_mtime:
            text = log.read_text(encoding='utf-8', errors='replace')
            if re.search(r'ModLoadingException|NoSuchMethodError|NoClassDefFoundError|ReportedException|Exception in thread|CLAY_GAMEPLAY_SMOKE_FAILED', text):
                raise RuntimeError('Minecraft startup reported a loading error; inspect the client output above.')
            if re.search(r'Created:? .*atlas', text, re.IGNORECASE) and 'CLAY_RENDER_STATE_STORAGE_OK' in text and 'CLAY_GAMEPLAY_SMOKE_OK:' in text:
                ready_since = ready_since or time.monotonic()
                if time.monotonic() - ready_since >= 5:
                    success = True
                    break
        if process.poll() is not None:
            raise RuntimeError(f'Client exited before resource loading completed (exit {process.returncode}).')
        time.sleep(1)
    if not success:
        raise RuntimeError('Client startup timed out before resource loading completed.')
    print('Client startup, render-state storage, world creation, mounted entity rendering, client death callbacks and authoritative server drops passed.', flush=True)
finally:
    if process.poll() is None:
        os.killpg(process.pid, signal.SIGTERM)
        try:
            process.wait(timeout=10)
        except subprocess.TimeoutExpired:
            os.killpg(process.pid, signal.SIGKILL)
            process.wait()
