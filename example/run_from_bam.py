# Example parameters running from BAM files
# Will read input bam and sample id from "filenames_rem.txt" at line "sys.argv[1]"
# and do all processing. This is custom script for running via qsub on Sun Grid Enginge
# so this is for reference only & to have a look on parameters.

import sys
import os
fileNo = int(sys.argv[1]) - 1
wdir = "/home/mshugai/tcga"
print "Working directory:", wdir
print "mkdir %s" % wdir 
fp=open('%s/filenames_rem.txt' % wdir)
lines=fp.readlines()
fp.close()
print "Task:", fileNo + 1
input_bam, sample_id = lines[fileNo].rstrip("\n\r").split("\t")
print "Input:", input_bam
print "Output:", sample_id
outdir = "%s/output" % wdir
os.system("mkdir %s" % outdir)
sampledir = "%s/%s" % (wdir, sample_id)
os.system("mkdir %s" % sampledir)
bindir = "%s/bin" % wdir
os.system("time %s/samtools collate -f -u -o %s/%s.bam %s" % (bindir, sampledir, sample_id, input_bam))
os.system("time %s/samtools fastq -1 %s/%s_R1.fastq -2 %s/%s_R2.fastq -0 /dev/null -s /dev/null %s/%s.bam" % (bindir, sampledir, sample_id, sampledir, sample_id, sampledir, sample_id))
os.system("time java -Xmx12G -Djava.util.concurrent.ForkJoinPool.common.parallelism=4 -jar %s/ilrfilter-1.0-SNAPSHOT.jar hash -S hsa -I %s/%s_R1.fastq %s/%s_R2.fastq -O %s/filtered_%s" % (bindir, sampledir, sample_id, sampledir, sample_id, sampledir, sample_id))
os.system("rm -rf %s/%s_R*.fastq" % (sampledir, sample_id))
os.system("time %s/mixcr -Xmx12G analyze shotgun -s hsa --starting-material RNA --contig-assembly --impute-germline-on-export --align \"--threads 4\" --assemble \"-OseparateByV=false\" %s/filtered_%s_R1.fastq %s/filtered_%s_R2.fastq %s/%s" % (bindir, sampledir, sample_id, sampledir, sample_id, sampledir, sample_id))
os.system("cp %s/*ALL* %s/*report %s/" % (sampledir, sampledir, outdir))
os.system("rm -rf %s" % sampledir)