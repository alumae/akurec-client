
Sample client that communicates with the AKU decoder online-demo.

== Usage ==

= Prerequisites =

Ant, Java 1.6 (maybe 1.5 works too)

= Compile =

> ant jar

= Execute =

> java -cp build/dist/lib/akurec-client.jar ee.ioc.phon.akurec.client.AudioFileRecognizer ~/tools/online-demo/data/rec.sh data/tarjahalonen_16k.wav

Change to your own location of rec.sh in the above command.

The wav file should be 16 kHz, mono, 16bit.