package com.matteobreganni.mypasswords.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import java.util.ArrayList;
import java.util.List;

import com.matteobreganni.mypasswords.R;
import utils.SlideItem;
import utils.SliderAdapter;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 sliderViewPager;
    private SliderAdapter sliderAdapter;
    private List<SlideItem> slideItems;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private Button sliderNextButton, sliderSkipButton, sliderBackButton;
    private ImageButton githubButton;
    private SignInButton googleSignInButton;
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
        githubButtonCardView = findViewById(R.id.githubButtonCardView);
        githubButton = findViewById(R.id.githubButton);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        for (int i = 0; i < googleSignInButton.getChildCount(); i++) {
            View v = googleSignInButton.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTextSize(18);  // Set your desired text size here
                break;
            }
        }

        slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        slideItems = new ArrayList<>();
        slideItems.add(new SlideItem(R.drawable.ic_baseline_vpn_key_24, "Effortless Password Generation & Retrieval", "Generate passwords easily with just the service name and your secret key!" +
                "\n\nThe secret key is the only password you’ll ever need to remember." +
                "\n\nServices are saved to simplify the password's retrieval and to avoid any typos that would generate a wrong password."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_new_label_24, "Streamline Password Management with Aliases", "Simplify your password retrieval by adding aliases to your services." +
                "\n\nFor example, on the \"Google\" service, you could add \"Youtube\" and \"Gmail\" as aliases, since they all have the same password that has been generated through the \"Google\" service name."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_switch_account_24, "Manage Multiple Profiles", "Create a different profile for each one of your emails." +
                "\n\nDifferent profiles will generate unique passwords for the same services, even with the same secret key. You can also use different secret keys across profiles for added security."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_lock_24, "Your Passwords, Safeguarded", "What is the safest way to store passwords? Not storing them at all!" +
                "\n\nMyPasswords allows you to regenerate your passwords anytime, without having to store them anywhere. You will be able to re-generate the same exact passwords by choosing the same service name and writing your secret key!" +
                "\n\nAlso, MyPasswords saves the data necessary to manage the app locally on your device. Nothing is saved in the cloud or shared in any way."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_info_24, "How It Works", "Each password is created by hashing the combination of the profile's email, the service's name and your secret key. " +
                "\n\nThis secure method ensures the same password is generated every time the same three parameters are used. Different emails, services or secret keys will generate different passwords. This means that only you are able to re-generate your passwords, because only you know the secret key!" +
                "\n\nFor example, \"email@email.com\", \"google\" and \"mypassword\" will always generate the password \"hq5Pf2I27s+Ty1i\" (try it yourself in the app!)."));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_code_24, "A Source-Available Project", "MyPasswords is free and it's source code is available on Github. If you're concerned about it's security, feel free to review the code and build the app yourself!" +
                "\n\nFor more information on how the app works, visit the GitHub page linked below." +
                "\n\n\nEnjoy the app, " +
                "\nMatteo Breganni"));
        slideItems.add(new SlideItem(R.drawable.ic_baseline_login_24, "Sign In", "Sign in with your Google account to securely back up your data to Google Drive." +
                "\n\nThis step is essential for sharing your data with our Windows app, allowing you to keep your services and profiles seamlessly synchronized." +
                "\n\nYour data will automatically sync every time you open the app or make any changes. You can also manually trigger a sync in the settings whenever needed."));

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
                    googleSignInButton.setVisibility(View.INVISIBLE);
                } else if (position == slideItems.size() - 2) {
                    sliderBackButton.setVisibility(View.VISIBLE);
                    sliderNextButton.setVisibility(View.VISIBLE);
                    sliderSkipButton.setVisibility(View.VISIBLE);
                    githubButtonCardView.setVisibility(View.VISIBLE);
                    googleSignInButton.setVisibility(View.INVISIBLE);

                    githubButtonCardView.startAnimation(fadeInAnimation);
                    googleSignInButton.clearAnimation();
                }else if (position == slideItems.size() - 1){
                    sliderBackButton.setVisibility(View.INVISIBLE);
                    sliderNextButton.setVisibility(View.INVISIBLE);
                    sliderSkipButton.setVisibility(View.INVISIBLE);
                    githubButtonCardView.setVisibility(View.INVISIBLE);
                    googleSignInButton.setVisibility(View.VISIBLE);

                    googleSignInButton.startAnimation(slideUpAnimation);
                    githubButtonCardView.clearAnimation();
                } else {
                    sliderBackButton.setVisibility(View.VISIBLE);
                    sliderNextButton.setVisibility(View.VISIBLE);
                    sliderSkipButton.setVisibility(View.VISIBLE);
                    githubButtonCardView.setVisibility(View.INVISIBLE);
                    googleSignInButton.setVisibility(View.INVISIBLE);

                    githubButtonCardView.clearAnimation();
                    googleSignInButton.clearAnimation();
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

        googleSignInButton.setOnClickListener(v -> {
            //TODO
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
