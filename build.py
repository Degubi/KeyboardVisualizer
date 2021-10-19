from subprocess import call
from shutil import rmtree
from os import remove, rename, environ, mkdir

JAVA_HOME = environ.get('JAVA_HOME')

call('"C:/Program Files (x86)/Microsoft Visual Studio/2019/Community/VC/Auxiliary/Build/vcvars64.bat" && ' +
    f'cl /LD /MD /Od /I "{JAVA_HOME}/include" /I "{JAVA_HOME}/include/win32" NativeUtils.c User32.lib "{JAVA_HOME}/lib/jawt.lib"')

remove('NativeUtils.obj')
remove('NativeUtils.lib')
remove('NativeUtils.exp')
rmtree('app')
mkdir('app')

rename('NativeUtils.dll', 'app/NativeUtils.dll')

print('\nDone!')