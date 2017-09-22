package com.charlie.widget.xrecyclerview.enumer;

/**
 * Created by Charlie on 2017/9/12.
 */
public enum ProgressStyle {

    BallPulse("BallPulseIndicator"),
    BallGridPulse("BallGridPulseIndicator"),
    BallClipRotate("BallClipRotateIndicator"),
    BallClipRotatePulse("BallClipRotatePulseIndicator"),
    SquareSpin("SquareSpinIndicator"),
    BallClipRotateMultiple("BallClipRotateMultipleIndicator"),
    BallPulseRise("BallPulseRiseIndicator"),
    BallRotate("BallRotateIndicator"),
    CubeTransition("CubeTransitionIndicator"),
    BallZigZag("BallZigZagIndicator"),
    BallZigZagDeflect("BallZigZagDeflectIndicator"),
    BallTrianglePath("BallTrianglePathIndicator"),
    BallScale("BallScaleIndicator"),
    LineScale("LineScaleIndicator"),
    LineScaleParty("LineScalePartyIndicator"),
    BallScaleMultiple("BallScaleMultipleIndicator"),
    BallPulseSync("BallPulseSyncIndicator"),
    BallBeat("BallBeatIndicator"),
    LineScalePulseOut("LineScalePulseOutIndicator"),
    LineScalePulseOutRapid("LineScalePulseOutRapidIndicator"),
    BallScaleRipple("BallScaleRippleIndicator"),
    BallScaleRippleMultiple("BallScaleRippleMultipleIndicator"),
    BallSpinFadeLoader("BallSpinFadeLoaderIndicator"),
    LineSpinFadeLoader("LineSpinFadeLoaderIndicator"),
    TriangleSkewSpin("TriangleSkewSpinIndicator"),
    Pacman("PacmanIndicator"),
    BallGridBeat("BallGridBeatIndicator"),
    SemiCircleSpin("SemiCircleSpinIndicator");

    public final String value;

    ProgressStyle(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString()+":"+value;
    }
}
