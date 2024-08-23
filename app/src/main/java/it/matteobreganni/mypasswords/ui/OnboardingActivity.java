package it.matteobreganni.mypasswords.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.matteobreganni.mypasswords.R;
import utils.SlideItem;
import utils.SliderAdapter;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 sliderViewPager;
    private SliderAdapter sliderAdapter;
    private List<SlideItem> slideItems;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private Button sliderNextButton, sliderSkipButton, sliderBeginButton, sliderBackButton;
    private ImageButton githubButton;
    private CardView githubButtonCardView;
    private boolean firstLoad = true;
    private Animation slideUpAnimation, fadeInAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        sliderViewPager = findViewById(R.id.sliderViewPager);
        dotsLayout = findViewById(R.id.dotsLayout);
        sliderNextButton = findViewById(R.id.sliderNextButton);
        sliderSkipButton = findViewById(R.id.sliderSkipButton);
        sliderBackButton = findViewById(R.id.sliderBackButton);
        sliderBeginButton = findViewById(R.id.sliderBeginButton);
        githubButtonCardView = findViewById(R.id.githubButtonCardView);
        githubButton = findViewById(R.id.githubButton);

        slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        slideItems = new ArrayList<>();
        slideItems.add(new SlideItem(R.drawable.ic_baseline_vpn_key_24, "Generate and retrieve passwords easily", "To generate a password, all you need is the name of the service you're creating a password for and your secret key!" +
                "\n\nThe secret key is the only password you will need to choose and remember, in order to generate and retrieve your passwords." +
                "\n\nServices will be saved to simplify the retrieval of passwords when you need it, to avoid misspelling the service's name and getting a wrong password."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_new_label_24, "Simplify password retrievals through aliases", "Adding aliases to your services will simplify their research when you need them!" +
                "\n\nFor example, on the \"Google\" service, you could add \"Youtube\" and \"Gmail\" as aliases, since they all have the same password that has been generated through the \"Google\" service name."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_switch_account_24, "Manage multiple profiles", "Create a different profile for each one of your emails." +
                "\n\nDifferent profiles will generate different passwords for the same services, even if the secret key is the same! If you wish, you can also use different secret keys across different profiles."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_lock_24, "Keep your passwords safe", "What is the safest way to store passwords? Not storing them!" +
                "\n\nThanks to the generation mechanism none of the generated passwords have to be saved anywhere, because you will be able to re-generate the same exact passwords by choosing the same service name and writing your secret key!" +
                "\n\nAlso, MyPasswords saves the necessary data to manage the app locally on your device, nothing is saved in the cloud or shared in any way."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_info_24, "How does it work?", "Each password is created by hashing the combination of the profile's email, the service you need a password for, and a secret key you choose. " +
                "\n\nThe hash mechanism ensures the same password is generated every time the same three parameters are used. Different emails, services or secret keys will generate different passwords. This means that only you are able to re-generate your passwords" +
                "\n\nFor example, \"email@email.com\", \"google\" and \"mypassword\" will always generate the password \"hq5Pf2I27s+Ty1i\" (try it yourself in the app!)."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_code_24, "A source-available project", "This app is free and it's source code is available on Github. If you're not sure about it's security, feel free to check the code and build the app yourself!" +
                "\n\nIf you need any more info on how the app works, visit the github page below!" +
                "\n\n\nEnjoy the app, " +
                "\nMatteo Breganni"));

        sliderAdapter = new SliderAdapter(this, slideItems);
        sliderViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);
        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (!firstLoad) {
                    addDotsIndicator(position);
                }
                firstLoad = false;

                if (position == 0){
                    sliderBackButton.setVisibility(View.INVISIBLE);
                    sliderNextButton.setVisibility(View.VISIBLE);
                    sliderSkipButton.setVisibility(View.VISIBLE);
                    githubButtonCardView.setVisibility(View.INVISIBLE);
                    sliderBeginButton.setVisibility(View.INVISIBLE);
                } else if (position == slideItems.size() - 1) {
                    sliderBackButton.setVisibility(View.INVISIBLE);
                    sliderNextButton.setVisibility(View.INVISIBLE);
                    sliderSkipButton.setVisibility(View.INVISIBLE);
                    githubButtonCardView.setVisibility(View.VISIBLE);
                    sliderBeginButton.setVisibility(View.VISIBLE);

                    githubButtonCardView.startAnimation(fadeInAnimation);
                    sliderBeginButton.startAnimation(slideUpAnimation);
                } else {
                    sliderBackButton.setVisibility(View.VISIBLE);
                    sliderNextButton.setVisibility(View.VISIBLE);
                    sliderSkipButton.setVisibility(View.VISIBLE);
                    githubButtonCardView.setVisibility(View.INVISIBLE);
                    sliderBeginButton.setVisibility(View.INVISIBLE);

                    githubButtonCardView.clearAnimation();
                    sliderBeginButton.clearAnimation();
                }
            }
        });

        sliderBackButton.setOnClickListener(v -> {
            int currentItem = sliderViewPager.getCurrentItem();
            if (currentItem > 0) {
                sliderViewPager.setCurrentItem(currentItem - 1);
            }
        });

        sliderNextButton.setOnClickListener(v -> {
            int currentItem = sliderViewPager.getCurrentItem();
            if (currentItem < slideItems.size() - 1) {
                sliderViewPager.setCurrentItem(currentItem + 1);
            }
        });

        sliderSkipButton.setOnClickListener(v -> {
            sliderViewPager.setCurrentItem(slideItems.size() - 1, true);
        });

        sliderBeginButton.setOnClickListener(v -> {
            introductionHasBeenShown();
            navigateToMainActivity();
        });

        githubButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(android.net.Uri.parse("https://github.com/Bregaa/MyPasswordsAndroidApp"));
            startActivity(intent);
        });
    }

    private void addDotsIndicator(int position) {
        dots = new TextView[slideItems.size()];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText("\u2022");
            dots[i].setTextSize(35);
            dots[i].setTextColor(getColor(R.color.inactiveDotColor));
            dots[i].setScaleX(1.0f);
            dots[i].setScaleY(1.0f);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getColor(R.color.activeDotColor));

            dots[position].animate().scaleX(1.5f).scaleY(1.5f).setDuration(300).start();
        }
    }

    private void introductionHasBeenShown() {
        SharedPreferences prefs = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("hasIntroductionBeenShown", true);
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
