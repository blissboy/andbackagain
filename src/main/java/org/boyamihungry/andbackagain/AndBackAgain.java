package org.boyamihungry.andbackagain;

import net.jafama.FastMath;
import org.boyamihungry.managedvalues.ManagedValueManager;
import org.boyamihungry.managedvalues.controllers.OscillatorValueController;
import org.boyamihungry.managedvalues.controllers.ValueController;
import org.boyamihungry.managedvalues.valuegenerators.Oscillator;
import org.boyamihungry.managedvalues.valuegenerators.SinusoidalOscillatorBuilder;
import org.boyamihungry.managedvalues.values.ManagedValue;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by patwheaton on 12/11/16.
 */
public class AndBackAgain extends PApplet {

    public static final int WIDTH = 1800;
    public static final int HEIGHT = 1200;
    public static final int POINTS = 200;
    public static final int POINT_SPREAD = 3;
    public static final int HISTORY_HEIGHT = 100;

    // for debug
    boolean freerun = true;
    boolean stepping = false;
    boolean nextStep = true;

    ManagedValueManager mgr;

    List<Oscillator<? extends Number>> oscillators = new ArrayList<>();
    Map<String, List<Number>> pointMap = new HashMap<>();

    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    public void setup()  {
        frameRate(60);
        textFont(createFont("Arial", 28));
        mgr = new ManagedValueManager() {
            @Override
            public String getName() {
                return "demo";
            }
            @Override
            public <T extends Number> boolean validateController(ValueController<T> controller) {
                return true;
            }
        };

        Callable<Long> stepper = new Callable<Long>() {
            long value = 0;
            @Override
            public Long call() throws Exception {
                return value++;
            }
        };

        Callable<Long> frameCountStepper = new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return (long)frameCount;
            }
        };

        // oscillators
        Oscillator sin99 = new SinusoidalOscillatorBuilder<Float>().withFrequency(99f).withName("99").withTimecodeGetter(frameCountStepper).build();
        Oscillator sin33 = new SinusoidalOscillatorBuilder<Float>().withFrequency(33f).withName("33").withTimecodeGetter(frameCountStepper).build();
        Oscillator sin200 = new SinusoidalOscillatorBuilder<Float>().withFrequency(1200f).withName("200").withTimecodeGetter(frameCountStepper).build();
        Oscillator sin2 = new SinusoidalOscillatorBuilder<Float>().withFrequency(3f).withName("2").withTimecodeGetter(frameCountStepper).build();
        Oscillator stepping1200 = new SinusoidalOscillatorBuilder<Float>().withFrequency(1200f).withName("step 1200").withTimecodeGetter(stepper).build();
        Oscillator stepping1 = new SinusoidalOscillatorBuilder<Float>().withFrequency(800f).withName("step 800").withTimecodeGetter(stepper).build();
        Oscillator stepping2 = new SinusoidalOscillatorBuilder<Float>().withFrequency(500f).withName("step 500").withTimecodeGetter(stepper).build();


        try {
            // variables that control what happens as output
            ManagedValue<Float> exitSize =              mgr.createManagedValue("exitSize", 20f, 40f, 20f, this);
            exitSize.addValueController(new OscillatorValueController<>(exitSize, sin200));
            exitSize.setValueController(exitSize.getAvailableValueControllers().stream().findFirst().get());

            ManagedValue<Float> worldRadius =           mgr.createManagedValue("worldRadius", 299f, 1200f, 1199f, this);
            worldRadius.addValueController(new OscillatorValueController<>(worldRadius, sin200));
            worldRadius.setValueController(worldRadius.getAvailableValueControllers().stream().findFirst().get());

            ManagedValue<Integer> depthOfPassage =      mgr.createManagedValue("depthOfPassage", 100, 900, 500, this);
            depthOfPassage.addValueController(new OscillatorValueController<>(depthOfPassage, sin200));
            depthOfPassage.setValueController(depthOfPassage.getAvailableValueControllers().stream().findFirst().get());

            ManagedValue<Integer> wallSwirlCount =      mgr.createManagedValue("wallSwirlCount", 20, 200, 200, this);
            //ManagedValue<Float> wallSwirlCount =      mgr.createManagedValue("wallSwirlCount", 50f, 500f, 300f, this);
            wallSwirlCount.addValueController(new OscillatorValueController<>(wallSwirlCount, sin99));
            wallSwirlCount.setValueController(wallSwirlCount.getAvailableValueControllers().stream().findFirst().get());

            ManagedValue<Integer> wallSwirlRadius =     mgr.createManagedValue("wallSwirlRadius", 01, 700, 190, this);
            wallSwirlRadius.addValueController(new OscillatorValueController<>(wallSwirlRadius, sin200));
            wallSwirlRadius.setValueController(wallSwirlRadius.getAvailableValueControllers().stream().findFirst().get());

            ManagedValue<Integer> depthLayers =         mgr.createManagedValue("depthLayers", 3, 100, 15, this);;
            depthLayers.addValueController(new OscillatorValueController<>(depthLayers, sin200));
            depthLayers.setValueController(depthLayers.getAvailableValueControllers().stream().findFirst().get());

            ManagedValue<Float> spinner255No1 =            mgr.createManagedValue("spinner255No1", 20f, 235f, 128f, this);;
            spinner255No1.addValueController(new OscillatorValueController<>(spinner255No1, stepping1200));
            spinner255No1.setValueController(spinner255No1.getAvailableValueControllers().stream().findFirst().get());
            ManagedValue<Float> spinner255No2 =            mgr.createManagedValue("spinner255No2", 0f, 235f, 128f, this);;
            spinner255No2.addValueController(new OscillatorValueController<>(spinner255No2, stepping1));
            spinner255No2.setValueController(spinner255No2.getAvailableValueControllers().stream().findFirst().get());
            ManagedValue<Float> spinner255No3 =            mgr.createManagedValue("spinner255No3", 0f, 235f, 128f, this);;
            spinner255No3.addValueController(new OscillatorValueController<>(spinner255No3, stepping2));
            spinner255No3.setValueController(spinner255No3.getAvailableValueControllers().stream().findFirst().get());

            ManagedValue<Float> xMod =           mgr.createManagedValue("xMod", 0.5f, 1.5f, 1f, this);
            xMod.addValueController(new OscillatorValueController<>(xMod, sin200));
            xMod.setValueController(xMod.getAvailableValueControllers().stream().findFirst().get());

            ManagedValue<Float> yMod =           mgr.createManagedValue("yMod", 0.5f, 1.5f, 1f, this);
            yMod.addValueController(new OscillatorValueController<>(yMod, stepping2));
            yMod.setValueController(yMod.getAvailableValueControllers().stream().findFirst().get());



            // this type of thing should be done in the manager
            //pointMap.put(exitSize.getKey(), new ArrayList<>(POINTS));


        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void draw() {

        if ( freerun || nextStep ) {
            long start = System.currentTimeMillis();
            float exitSize = mgr.getManagedValue("exitSize").getValue().floatValue();
            float wallSwirlCount = mgr.getManagedValue("wallSwirlCount").getValue().intValue();
            float worldRadius = mgr.getManagedValue("worldRadius").getValue().floatValue();
            int depthOfPassage = mgr.getManagedValue("depthOfPassage").getValue().intValue();
            int wallSwirlRadius = mgr.getManagedValue("wallSwirlRadius").getValue().intValue();
            int depthLayers = mgr.getManagedValue("depthLayers").getValue().intValue();
            int colorVal1 = mgr.getManagedValue("spinner255No1").getValue().intValue();
            int colorVal2 = mgr.getManagedValue("spinner255No2").getValue().intValue();
            int colorVal3 = mgr.getManagedValue("spinner255No3").getValue().intValue();
            int balancedColor = color(colorVal1, colorVal2, colorVal3);

            int worldColor = color(255, 0, 0);
            int swirlColor = color(0, 255, 0);


            background(128);
            pushMatrix();
            translate(width / 2, height / 2);
            pushStyle();

            stroke(255);
            strokeWeight(3f);
            text("wooooooooo" + frameRate, 0, 0);

            drawExit(exitSize);
            drawWorld(worldRadius, worldColor);
            drawSpokes(0,TWO_PI,20,400);

            stroke(balancedColor);
            strokeWeight(1f);
            drawCirclesOnSpokes(0, TWO_PI,20, 9, wallSwirlRadius, 400, mgr.getManagedValue("xMod").getValue().floatValue(), mgr.getManagedValue("yMod").getValue().floatValue());

            strokeWeight(1f);

            //long startLoop = drawSwirl(exitSize, wallSwirlCount, worldRadius, wallSwirlRadius, depthLayers, swirlColor);

            popStyle();
            popMatrix();

            pushMatrix();
            mgr.getManagedValues().forEach(mv -> {
                line(0, 0, 300, 0);
                translate(0, mv.drawControlPanel().y);
            });
            popMatrix();

            nextStep = false;
        }
    }

    private long drawSwirl(float exitSize, float wallSwirlCount, float worldRadius, int wallSwirlRadius, int depthLayers, int swirlColor) {
        float r = worldRadius - exitSize;
        float rStep = r / (depthLayers + 1);
        int adornmentCount = 0;

        float theta = 0;
        float costheta;
        float sintheta;

        long startLoop = System.currentTimeMillis();
        stroke(swirlColor);
        do {
            costheta = (float) FastMath.cos(theta);
            sintheta = (float)FastMath.sin(theta);

            for (int layerCount=0; layerCount<depthLayers; layerCount++) {
                ellipse(((exitSize / 2) + (rStep * layerCount)) * costheta,
                        ((exitSize / 2) + (rStep * layerCount)) * sintheta,
                        wallSwirlRadius,
                        wallSwirlRadius);
            }
            theta = theta + ((2f * PI) / (wallSwirlCount / (float) depthLayers));

            adornmentCount++;

        } while (adornmentCount < (wallSwirlCount / depthLayers));
        return startLoop;
    }

    private void drawWorld(float worldRadius, int worldColor) {
        // worldradius
        noFill();
        stroke(worldColor);
        ellipse(0, 0, worldRadius, worldRadius);
    }

    private void drawExit(float exitSize) {
        // exit
        ellipse(0, 0, exitSize * 2, exitSize * 2);
    }

    private void drawStyledSpokes(Stylizer stylizer, float startAngle, float endAngle, int numberOfSpokes, float length) {
        try {
            pushMatrix();
            stylizer.setStyle();
            drawSpokes(startAngle,endAngle,numberOfSpokes,length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            popMatrix();
        }
    }


    private void drawSpokes(float startAngle, float endAngle, int numberOfSpokes, float length) {
        PVector spoke;
        for ( float curAngle = startAngle; curAngle <= endAngle; curAngle += (endAngle-startAngle) / (float)numberOfSpokes ) {
            spoke = PVector.fromAngle(curAngle).mult(length);
            line(0,0,spoke.x,spoke.y);
        }
    }

    private void drawCirclesOnSpokes(float startAngle, float endAngle, int numberOfSpokes, int circlesPerSpoke, float radius, float spokeLength, float xMod, float yMod) {
        PVector spoke;
        PVector ptOnSpoke;
        for ( float curAngle = startAngle; curAngle <= endAngle; curAngle += (endAngle-startAngle) / (float)numberOfSpokes ) {
            spoke = PVector.fromAngle(curAngle).mult(spokeLength);
            line(0,0,spoke.x,spoke.y);
            for (float i=0; i<circlesPerSpoke; i++) {
                ellipse(spoke.x / i, spoke.y / i, radius * xMod, radius * yMod);
            }
        }
    }





    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"org.boyamihungry.andbackagain.AndBackAgain"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }

    @Override
    public void keyPressed() {
        super.keyPressed();
        if ( key == 'b' || key == 'B' ) {
            freerun = !freerun;
        } else {
            if (key == ' '){
                nextStep = true;
            }
        }
    }
}
