/**
* <h1>PlayTune</h1>
* The PlayTune program builds music that is randomly generated.
* from the basis of several popular chord progressions. It's truly a one-of-a-kind musical experience
* If you want to PLAY THIS TUNE...
* First, compile playTune.java and all the necessarry STD classes using "javac" followed by the java file
* Next, type "java playTune < tune.txt"
* Functions: pixel, sum, tone, note, note2, harmonic, harmonicPlayer, verse, majorScale,
* minorScale, majorChord, minorChord, changeVolume, echo, combine, time, volume, changeScale
* Controls: Use "w" and "s" to increase and decrease volume,
* set musicalEffect to true for our unique "electric guitar vibe"
* @author  Emily Mattlin, Jeremy Ben-Meir
* @version 1.0
* @since   2016-12-17
*/

import java.io.*;

public class PlayTune {

    static double height = 1;
    static double width = 0;
    static double amplitude = 1;
    static int blue=StdRandom.uniform(0,256);
    static double sumTime = 0;
    static boolean musicalEffect = false;

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This main method is used to build the visual and call the methods to write and read the music.
    * The main method also initializes the variables later used for parameters, including the chord progressions.
    * @param args This parameter is not used for PlayTune and is ignored
    */
    public static void main(String[] args) {

        int[][] verseF = new int[][]{ //Potential verse progressions
            { 3, 10, 12, 8 },
            { 3, 10, 12, 19, 8 },
            { 12, 10, 8, 12 },
            { 3, 12, 8, 10 },
            { 3, 8, 12, 10 },
            { 3, 10, 8, 10 }
        };

        double[] addingSounds = {};
        boolean showPic = false;
        double stepSize = 1.0;
        int chordProgV = StdRandom.uniform(verseF.length);

        mystery();

        StdDraw.setCanvasSize(600,600); //Set up visualizer
        StdDraw.setPenColor(0,0,0);
        StdDraw.filledRectangle(0.5,0.5,0.5,0.5);
        int[] stepsV = new int [verseF[chordProgV].length*8];
        for (int thing = 0; thing < verseF[chordProgV].length; thing++){
            for (int cou = 0; cou < 8; cou++){
                stepsV[cou+(thing*8)]=verseF[chordProgV][thing];
            }
        }
        try{
            PrintWriter pw = new PrintWriter("tune.txt");

            for(int co = 0; co < 15; co++){
                for(int count = 0; count < verseF[chordProgV].length; count++){
                    double total=0;
                    while(total<1){
                        int randVar;

                        if (total == 0.625) randVar = StdRandom.uniform(2,4);
                        else if (total == 0.75) randVar = StdRandom.uniform(2,4);
                        else if (total == 0.875) randVar = StdRandom.uniform(2,3);
                        else if (total == 1) break;
                        else randVar = StdRandom.uniform(2,5);

                        verse(pw,verseF[chordProgV][count], randVar);

                        if (randVar == 2) total += 0.125;
                        else if (randVar == 3) total += 0.25;
                        else if (randVar == 4) total += 0.5;
                    }
                }
            }
            pw.close();
            } catch (Exception ex) {
            ex.printStackTrace();
        }

        StdAudio.play(echo(10, note2(3, 0.5, 1), 0.5, 0.25));
        int rollThrough=0;
        while (!StdIn.isEmpty()) { /////Read in pitch-duration pairs from standard input

            int pitch = StdIn.readInt();
            double duration = StdIn.readDouble();
            double[] a = note2(pitch, duration, amplitude);
            double[] b = note(stepsV[rollThrough], 0.125, amplitude);

            pixel(pitch,a[1]);
            volume(amplitude);
            time(duration);


            if (StdDraw.mousePressed()){
                showPic = !showPic;
            }
            if (!musicalEffect){
            double[][] everything = {addingSounds,sum(note2(pitch, duration, 1),note(stepsV[rollThrough], 0.125, 1),0.3,0.7)};
            addingSounds = combine(everything);
          } else {
            double[][] everything = {addingSounds,sum(note2(pitch, duration, 2),note(stepsV[rollThrough], 0.125, 2),0.3,0.7)};
            addingSounds = combine(everything);
          }
            StdAudio.play(sum(a,b,0.3,0.7));

            if(StdDraw.isKeyPressed(87)) changeVolume("up",0.08);
            else if(StdDraw.isKeyPressed(83)) changeVolume("down",0.08);// Volume changer

            rollThrough++;
            if(rollThrough==(verseF[chordProgV].length*8)-1)rollThrough=0;
        }

        StdOut.println("Saving to wav....");
        StdAudio.save("Bubble.wav", addingSounds);
        StdOut.println("Saved!");
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method draws a visual that corresponds to the music being played
    * @param pitch This is used for setting pen color
    * @param duration This is used for setting rectangle height
    */
    public static void pixel(int pitch, double duration){
        StdDraw.setPenColor((int)((pitch)*3)+30,(int)((pitch)*3)+30, blue);//(255/12.0),(int)(pitch)(255/12.0),(int)(pitch)(255/12.0));
        StdDraw.filledRectangle(width, height, 0.05, duration/4);
        width+=0.1;
        if(width>=1){
            height-=0.1;
            width = 0;
        }
        if(height<=0){
            StdDraw.clear(StdDraw.BLACK);
            height=1;
            blue=StdRandom.uniform(0,256);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method returns the weighted sum of two arrays
    * @param a This is the first array
    * @param b This is the second array
    * @param awt This is the value a is weighted by
    * @param bwt This is the value a is weighted by
    * @return double[] This method returns an array[] of the weighted sums
    */
    public static double[] sum(double[] a, double[] b, double awt, double bwt) { /////// take weighted sum of two arrays
        // precondition: arrays have the same lnotength
        assert a.length == b.length;
        // compute the weighted sum
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i]*awt + b[i]*bwt;
        }
        return c;
    }


    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method creates a pure tone of the given frequency for the given duration
    * @param hz This is the frequency value
    * @param duration This is length a note is to be played
    * @param sound This is the amplitude value
    * @return double[] This method returns an array[] comprized of pure tones
    */
    public static double[] tone(double hz, double duration, double sound) { /////// create a pure tone of the given frequency for the given duration
        int n = (int) (StdAudio.SAMPLE_RATE * duration);
        double[] a = new double[n+1];
        for (int i = 0; i <= n; i++) {
            a[i] = sound*Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
        }
        return a;
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method creates a note with the given pitch for the chord progressions
    * @param pitch This is the pitch value
    * @param t This is length a note is to be played
    * @param sound This is the amplitude value
    * @return double[] This method returns an array[] of the sum of values to be played
    */
    public static double[] note(int pitch, double t, double sound) { /////// create a note with harmonics of of the given pitch, where 0 = concert A
        double value1;
        double value2;
        if(pitch>11) {
            pitch=pitch-12;
            value1 = pitch + 3;
            value2 = pitch + 7;
            } else {
            value1 = pitch + 4;
            value2 = pitch + 7;
        }

        double hz = 440.0 * Math.pow(2, (pitch-12) / 12.0);
        double hz1 = 440.0 * Math.pow(2, (value1-12) / 12.0);
        double hz2 = 440.0 * Math.pow(2, (value2-12) / 12.0);
        double hz3= 440.0 * Math.pow(2, (10) / 12.0);///////////

        double[] a  = tone((hz), t, sound);
        double[] hi = tone(hz1, t, sound);
        double[] lo = tone(hz2, t, sound);
        double[] third = tone(hz3, t, sound);/////////
        double[] h  = sum(hi, lo, 0.5, 0.5);

        return sum(sum(a, h, 0.5, 0.5),sum(a, h, 0.5, 0.5),0.4,0.6);

    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method creates notes with the given pitch for the melody
    * @param pitch This is the pitch value
    * @param t This is length a note is to be played
    * @param sound This is the amplitude value
    * @return double[] This method returns an array[] of the sum of values to be played
    */
    public static double[] note2(int pitch, double t, double sound) { /////// create a note with harmonics of of the given pitch, where 0 = concert A

        System.out.println(pitch);
        System.out.println(t);
        double hz = 440.0 * Math.pow(2, (pitch-12) / 12.0);
        double hz3= 440.0 * Math.pow(2, (10) / 12.0);///////////

        double[] a  = tone((hz), t, sound);
        double[] third = tone(hz3, t, sound);/////////

        return a;

    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method returns the values used to make harmonies
    * @param pitch This is the pitch value
    * @return int[] This method returns an array[] of the sum of values to be played
    */
    public static int[] harmonic(int pitch) {
        int[] highLow = new int [2];
        highLow[0]=pitch-12;
        highLow[1]=pitch+12;
        return highLow;
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method creates a note with the given pitch for the melody and harmony
    * @param pitch This is the pitch value
    * @param t This is duration value
    * @param sound This is the amplitude value
    * @return int[] This method returns an array[] of the sum of values to be played as a harmony
    */
    public static double[] harmonicPlayer(int pitch, double t, double sound) { /////// create a note with harmonics of of the given pitch, where 0 = concert A

        System.out.println(pitch);
        System.out.println(t);
        int[] smallRabbit = harmonic(pitch);
        double hz = 440.0 * Math.pow(2, (pitch-12) / 12.0);
        double hz1= 440.0 * Math.pow(2, (smallRabbit[0]-12) / 12.0);
        double hz2= 440.0 * Math.pow(2, (smallRabbit[1]-12) / 12.0);
        double[] a = tone(hz,  t, sound);
        double[] b = tone(hz1, t, sound);
        double[] c = tone(hz2, t, sound);

        return sum(sum(a, b, 0.5, 0.5),c,0.666,0.333);

    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method creates a note with the given pitch for the melody and harmony
    * @param pw This is the value written and played
    * @param curChord This is chord
    * @param times This value determines duration
    */
    public static void verse(PrintWriter pw, int curChord, int times) {
        if (times == 1){ times = 1;}
        else if (times == 3){times = 4;}
        else if (times == 4){ times = 8;}
        else if (times == 2){ times = 2;}
        int[] scale = new int [5];
        int rando = StdRandom.uniform(scale.length);

        if (curChord > 11){
            curChord-=12;
            scale = minorScale(curChord);
            } else {
            scale = majorScale(curChord);
        }

        for (int rep = 0; rep < times; rep++){
            try{
                pw.println( scale[rando]+12 + " " + (( (int) (Math.random() * (2 - 1) + 1) ) * 0.125));
                } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method returns the major scale of the value inputted
    * @param pitch This is the pitch
    * @return an array of the scale of the pitch used
    */
    public static int[] majorScale(int pitch) {
        int[] major = {pitch,pitch+2,pitch+4,pitch+7,pitch+9};
        return(major);
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method returns the minor scale of the value inputted
    * @param pitch This is the pitch
    * @return int[] An array of the scale of the pitch used
    */
    public static int[] minorScale(int pitch) {
        int[] minor = {pitch,pitch+3,pitch+5,pitch+7,pitch+10};
        return(minor);
    }

    //////////////////////////////////////////////////////////////////////////////////
    /**
    * This method returns the major chord of the value inputted
    * @param pitch This is the pitch
    * @return int[] An array of the chord of the pitch used
    */
    public static int[] majorChord(int pitch) {
        int[] major = {pitch+4,pitch+7};
        return(major);
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method returns the minor scale of the value inputted
    * @param pitch This is the pitch
    * @return int[] Array of the scale of the pitch used
    */
    public static int[] minorChord(int pitch) {
        int[] minor = {pitch+3,pitch+7};
        return(minor);
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method adjusts volume based on "w" and "s" selected
    * @param direction This is the keys inputted
    * @param stepSize This is how much the amplitude will increase or decrease
    */
    public static void changeVolume(String direction,double stepSize) {
        if(direction=="up" && amplitude<=1.2&&!musicalEffect){ //up
            amplitude+=stepSize;
        }
        else if(direction=="down" && amplitude>0.08&&!musicalEffect){ //down
            amplitude-=stepSize;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method combines arrays from a 2-D array into a 1-D array
    * @param input This is the 2-D array of keys
    * @return double[] A 1-D array of the combined arrays from a 2-D array
    */
    public static double[] combine(double[][] input) {
        int addedLength = 0;
        for (int i = 0; i < input.length; i++) {
            addedLength += input[i].length;
        }
        double[] output = new double[addedLength];
        int x = 0;
        for (int y = 0; y < input.length; y++) {
            for (int z = 0; z < input[y].length; z++) {
                output[x] += input[y][z];
                x++;
            }
        }
        return output;
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method prints the length of time in seconds on the visual
    * @param duration This is the duration of each note
    */
    public static void time(double duration){
        sumTime += duration;
        int min = 0;
        int sec = 0;
        if(sumTime >=60){
            min=1;
            sec=(int)sumTime-60;
            } else if(sumTime >=120){
            min=2;
            sec=(int)sumTime-120;
            } else if(sumTime >=180){
            min=3;
            sec=(int)sumTime-180;
            } else {
            sec=(int)sumTime;
            min = 0;
        }
        if (sec>=10) StdDraw.text(0.5,0.05,min+":"+sec);
        else StdDraw.text(0.5,0.05,min+":0"+sec);
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method prints the percentage of volume played on the visual
    * @param amplitude This is the amplitude of each note
    */
    public static void volume(double amplitude){
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(0.5, 0, 0.11, 0.09);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.text(0.5,0.02,"Volume: "+((int)(amplitude*100))+"%");
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method returns an array of the echod sound
    * @param repetitions the number of repetitions
    * @param pitch the audio to echo
    * @param scale scale of volume decrease each echo
    * @param delay time between each echo
    * @return double[] An array of the echoed values
    * @throws ArithmeticException if delay is negative
    */
    public static double[] echo(int repetitions, double[] pitch, double scale, double delay) {
        if(delay<0) throw new ArithmeticException("delay cannot be 0");
        double[] delayed = new double[(int)(delay * StdAudio.SAMPLE_RATE)];
        for (int co = 0; co < delayed.length; co++) {
            delayed[co] = 0;
        }
        double[] echoed = pitch;
        for (int co = 0; co < repetitions; co++) {
            pitch = changeScale(pitch, scale);
            double[][] arrays = {echoed, delayed, pitch};
            echoed = combine(arrays);
        }
        return echoed;
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * Returns an array of the tone with a scaled amplitude
    * @param   pitch  the audio to scale
    * @param   scale  the scale factor for the amplitude
    * @return   double[] An array of the scaled values
    */
    public static double[] changeScale(double[] pitch, double scale) {
        double[] output;
        output = new double[pitch.length];
        for (int i = 0; i < pitch.length; i++) {
            output[i] = pitch[i] * scale;
        }
        return output;
    }

    //////////////////////////////////////////////////////////////////////////////////

    /**
    * This method is an enigma. Change musicalEffect to true to find out its funtion!
    */
    public static void mystery(){
        if (musicalEffect) amplitude = 2;
        else amplitude = 1;
    }
}
