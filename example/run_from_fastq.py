import sys
import os
fileNo = int(sys.argv[1]) - 1
wdir = "/home/molimm2/mishugay/victoria_rna_qsub"
print "Working directory:", wdir
print "mkdir %s" % wdir 
fp=open('%s/filenames_rem.txt' % wdir)
lines=fp.readlines()
fp.close()
print "Task:", fileNo + 1
fastq1, fastq2, sample_id = lines[fileNo].rstrip("\n\r").split("\t")
print "Input:", [fastq1, fastq2]
print "Output:", sample_id
outdir = "%s/output" % wdir
os.system("mkdir %s" % outdir)
sampledir = "%s/%s" % (wdir, sample_id)
os.system("mkdir %s" % sampledir)
bindir = "%s/bin" % wdir
os.system("time mixcr -Xmx12G analyze shotgun -s hsa --starting-material RNA --contig-assembly --impute-germline-on-export --align \"--threads 6\" --assemble \"-OseparateByV=false\" %s %s %s/%s" % (fastq1, fastq1, sampledir, sample_id))
os.system("cp %s/*ALL* %s/*report %s/" % (sampledir, sampledir, outdir))
os.system("rm -rf %s" % sampledir)