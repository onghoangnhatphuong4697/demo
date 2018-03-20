package com.framgia.music.screen.splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import com.framgia.music.R;
import com.framgia.music.screen.BaseActivity;
import com.framgia.music.screen.main.MainActivity;
import com.framgia.music.utils.Constant;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 3/19/2018.
 */

public class SplashScreenActivity extends BaseActivity {

    private static final int DELAY_2900 = 2900;
    private static final int DURATION_3000 = 3000;
    private static final int DURATION_2000 = 2000;
    private static final float TO_0 = 0.0f;
    private static final float TO_1 = 1.0f;
    private static final float TO_610 = -610f;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        initViews();
        handles();
    }

    private void initViews() {
        CircleImageView imageLogo = findViewById(R.id.image_logo);
        TextView textSlogan = findViewById(R.id.text_slogan);

        AnimationSet animationSetImage = new AnimationSet(true);
        RotateAnimation rotateAnimationImage =
                new RotateAnimation(Constant.FROM_0, Constant.TO_360, Animation.RELATIVE_TO_SELF,
                        Constant.PIVOT_0_5, Animation.RELATIVE_TO_SELF, Constant.PIVOT_0_5);
        rotateAnimationImage.setDuration(DURATION_3000);
        ScaleAnimation scaleAnimationImage =
                new ScaleAnimation(Constant.FROM_0, TO_1, Constant.FROM_0, TO_1);
        scaleAnimationImage.setDuration(Constant.DURATION_1500);
        animationSetImage.addAnimation(rotateAnimationImage);
        animationSetImage.addAnimation(scaleAnimationImage);
        imageLogo.startAnimation(animationSetImage);

        AnimationSet animationSetTextSlogan = new AnimationSet(true);
        TranslateAnimation translateAnimationText =
                new TranslateAnimation(Constant.FROM_0, TO_0, Constant.FROM_0, TO_610);
        translateAnimationText.setDuration(DURATION_3000);
        AlphaAnimation alphaAnimationText = new AlphaAnimation(Constant.FROM_0, TO_1);
        alphaAnimationText.setDuration(DURATION_2000);
        animationSetTextSlogan.addAnimation(translateAnimationText);
        animationSetTextSlogan.addAnimation(alphaAnimationText);
        textSlogan.startAnimation(animationSetTextSlogan);
    }

    private void handles() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, DELAY_2900);
    }
}
