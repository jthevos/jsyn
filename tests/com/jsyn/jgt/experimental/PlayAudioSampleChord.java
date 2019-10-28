package com.jsyn.jgt.experimental;

// PlayChord Imports
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.instruments.SubtractiveSynthVoice;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.UnitVoice;
import com.jsyn.util.VoiceAllocator;
import com.softsynth.math.AudioMath;
import com.softsynth.shared.time.TimeStamp;


// PlaySample Imports
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.data.FloatSample;
import com.jsyn.examples.PlaySample;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.VariableRateDataReader;
import com.jsyn.unitgen.VariableRateMonoReader;
import com.jsyn.unitgen.VariableRateStereoReader;
import com.jsyn.util.SampleLoader;


public class PlayAudioSampleChord {
    private Synthesizer synth;
    private VariableRateDataReader samplePlayer;
    private VariableRateDataReader samplePlayer2;
    private LineOut lineOut;
    private LineOut lineOut2;

    private void test() {

        TimeStamp t = new TimeStamp(0.0);
        
        URL sampleFile;
        try {
            sampleFile = new URL("http://www.softsynth.com/samples/Clarinet.wav");
            // sampleFile = new URL("http://www.softsynth.com/samples/NotHereNow22K.wav");
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
            return;
        }
        
        URL sampleFile2;
        try {
            sampleFile2 = new URL("http://www.softsynth.com/samples/NotHereNow22K.wav");
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
            return;
        }

        synth = JSyn.createSynthesizer();

        FloatSample sample2;
        FloatSample sample;
        try {
            // Add an output mixer.
            synth.add(lineOut = new LineOut());
            synth.add(lineOut2 = new LineOut());

            // Load the sample and display its properties.
            SampleLoader.setJavaSoundPreferred(false);
            sample = SampleLoader.loadFloatSample(sampleFile);
            System.out.println("Sample has: channels  = " + sample.getChannelsPerFrame());
            System.out.println("            frames    = " + sample.getNumFrames());
            System.out.println("            rate      = " + sample.getFrameRate());
            System.out.println("            loopStart = " + sample.getSustainBegin());
            System.out.println("            loopEnd   = " + sample.getSustainEnd());
            
            sample2 = SampleLoader.loadFloatSample(sampleFile2);
            System.out.println("Sample has: channels  = " + sample2.getChannelsPerFrame());
            System.out.println("            frames    = " + sample2.getNumFrames());
            System.out.println("            rate      = " + sample2.getFrameRate());
            System.out.println("            loopStart = " + sample2.getSustainBegin());
            System.out.println("            loopEnd   = " + sample2.getSustainEnd());

            if (sample.getChannelsPerFrame() == 1) {
                synth.add(samplePlayer = new VariableRateMonoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
            } else if (sample.getChannelsPerFrame() == 2) {
                synth.add(samplePlayer = new VariableRateStereoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
                samplePlayer.output.connect(1, lineOut.input, 1);
            } else {
                throw new RuntimeException("Can only play mono or stereo samples.");
            }
            
            if (sample2.getChannelsPerFrame() == 1) {
                synth.add(samplePlayer2 = new VariableRateMonoReader());
                samplePlayer2.output.connect(0, lineOut2.input, 0);
            } else if (sample2.getChannelsPerFrame() == 2) {
                synth.add(samplePlayer2 = new VariableRateStereoReader());
                samplePlayer2.output.connect(0, lineOut2.input, 0);
                samplePlayer2.output.connect(1, lineOut2.input, 1);
            } else {
                throw new RuntimeException("Can only play mono or stereo samples.");
            }

            // Start synthesizer using default stereo output at 44100 Hz.
            synth.start();

            samplePlayer.rate.set(sample.getFrameRate());

            // We only need to start the LineOut. It will pull data from the
            // sample player.
            lineOut.start();
            lineOut2.start();

            // We can simply queue the entire file.
            // Or if it has a loop we can play the loop for a while.
            if (sample.getSustainBegin() < 0) {
                System.out.println("queue the sample");
                samplePlayer.dataQueue.queue(sample);
            } else {
                System.out.println("queueOn the sample");
                samplePlayer.dataQueue.queueOn(sample, t);
                synth.sleepFor(8.0);
                System.out.println("queueOff the sample");
                samplePlayer.dataQueue.queueOff(sample);
            }

            // Wait until the sample has finished playing.
            do {
                synth.sleepFor(1.0);
            } while (samplePlayer.dataQueue.hasMore());

            synth.sleepFor(0.5);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        try {
            // Add an output mixer.
            synth.add(lineOut = new LineOut());

            // Load the sample and display its properties.
            SampleLoader.setJavaSoundPreferred(false);
            sample2 = SampleLoader.loadFloatSample(sampleFile2);
            System.out.println("Sample has: channels  = " + sample2.getChannelsPerFrame());
            System.out.println("            frames    = " + sample2.getNumFrames());
            System.out.println("            rate      = " + sample2.getFrameRate());
            System.out.println("            loopStart = " + sample2.getSustainBegin());
            System.out.println("            loopEnd   = " + sample2.getSustainEnd());

            if (sample2.getChannelsPerFrame() == 1) {
                synth.add(samplePlayer = new VariableRateMonoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
            } else if (sample2.getChannelsPerFrame() == 2) {
                synth.add(samplePlayer = new VariableRateStereoReader());
                samplePlayer.output.connect(0, lineOut.input, 0);
                samplePlayer.output.connect(1, lineOut.input, 1);
            } else {
                throw new RuntimeException("Can only play mono or stereo samples.");
            }

            // Start synthesizer using default stereo output at 44100 Hz.
            synth.start();

            samplePlayer.rate.set(sample2.getFrameRate());

            // We only need to start the LineOut. It will pull data from the
            // sample player.
            lineOut.start();

            // We can simply queue the entire file.
            // Or if it has a loop we can play the loop for a while.
            if (sample2.getSustainBegin() < 0) {
                System.out.println("queue the sample");
                samplePlayer.dataQueue.queue(sample2);
            } else {
                System.out.println("queueOn the sample");
                samplePlayer.dataQueue.queueOn(sample2, t);
                synth.sleepFor(8.0);
                System.out.println("queueOff the sample");
                samplePlayer.dataQueue.queueOff(sample2);
            }

            // Wait until the sample has finished playing.
            do {
                synth.sleepFor(1.0);
            } while (samplePlayer.dataQueue.hasMore());

            synth.sleepFor(0.5);

        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Stop everything.
        synth.stop();
    }

    public static void main(String[] args) {
        new PlayAudioSampleChord().test();
    }

}
