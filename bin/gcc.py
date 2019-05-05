import os
import urllib.request
import subprocess
import bz2
import shutil

ver = '8.3.0'
url = 'ftp://ftp.equation.com/gcc/gcc-{0}-64.exe'.format(ver)

print('Fetching ' + url)

urllib.request.urlretrieve(url, 'gcc.7z')

subprocess.run('7z x gcc.7z')

for root, directories, filenames in os.walk('source'):
    for filename in filenames:
        print('Extracting ' + filename)
        infile = os.path.join(root, filename)
        outfile = infile + '.tmp'
        with bz2.open(infile) as inf, open(outfile, 'wb') as outf:
            shutil.copyfileobj(inf, outf)
        os.remove(infile)
        os.rename(outfile, infile)

subprocess.run('7z a -r ../gcc-{0}.7z'.format(ver), cwd='source')
