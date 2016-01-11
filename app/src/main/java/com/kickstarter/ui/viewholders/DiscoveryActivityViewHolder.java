package com.kickstarter.ui.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kickstarter.KSApplication;
import com.kickstarter.R;
import com.kickstarter.libs.KSString;
import com.kickstarter.libs.transformations.CircleTransformation;
import com.kickstarter.models.Activity;
import com.kickstarter.models.Project;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiscoveryActivityViewHolder extends KSViewHolder {
  @Inject KSString ksString;

  protected @Bind(R.id.activity_click_area) LinearLayout activityClickArea;
  protected @Bind(R.id.activity_image) ImageView activityImageView;
  protected @Bind(R.id.activity_title) TextView activityTitleTextView;
  protected @Bind(R.id.activity_subtitle) TextView activitysubTitleTextView;
  protected @Bind(R.id.see_activity_button) Button seeActivityButton;
  protected @BindString(R.string.activity_friend_backed_project_name_by_creator_name) String categoryBackingString;
  protected @BindString(R.string.activity_user_name_is_now_following_you) String categoryFollowingString;
  protected @BindString(R.string.activity_follow_back) String categoryFollowBackString;
  protected @BindString(R.string.activity_project_was_not_successfully_funded) String categoryFailureString;
  protected @BindString(R.string.activity_user_name_launched_project) String categoryLaunchString;
  protected @BindString(R.string.activity_successfully_funded) String categorySuccessString;
  protected @BindString(R.string.activity_funding_canceled) String categoryCancellationString;
  protected @BindString(R.string.activity_posted_update_number_title) String categoryUpdateString;

  protected Activity activity;

  private final Delegate delegate;
  public interface Delegate {
    void discoveryActivityViewHolderSeeActivityClicked(DiscoveryActivityViewHolder viewHolder);
    void discoveryActivityViewHolderProjectClicked(DiscoveryActivityViewHolder viewHolder, Project project);
    void discoveryActivityViewHolderUpdateClicked(DiscoveryActivityViewHolder viewHolder, Activity activity);
  }

  public DiscoveryActivityViewHolder(final @NonNull View view, final @NonNull Delegate delegate) {
    super(view);
    this.delegate = delegate;

    ((KSApplication) view.getContext().getApplicationContext()).component().inject(this);
    ButterKnife.bind(this, view);
  }

  public void onBind(final @NonNull Object datum) {
    this.activity = (Activity) datum;

    final Context context = view.getContext();

    activityImageView.setVisibility(View.VISIBLE);
    activityTitleTextView.setVisibility(View.VISIBLE);
    activitysubTitleTextView.setVisibility(View.VISIBLE);

    LinearLayout.LayoutParams layoutParams =
      new LinearLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.grid_4),
        context.getResources().getDimensionPixelSize(R.dimen.grid_4));
    activityImageView.setLayoutParams(layoutParams);

    // temp until followable :
    activityClickArea.setBackground(context.getResources().getDrawable(R.drawable.click_indicator_light_masked, null));
    LinearLayout.LayoutParams titleLayoutParams = (LinearLayout.LayoutParams)activitysubTitleTextView.getLayoutParams();
    titleLayoutParams.topMargin = 0;
    activityTitleTextView.setLayoutParams(titleLayoutParams);

    if (activity.category().equals(Activity.CATEGORY_BACKING)) {
      Picasso.with(context).load(activity.user().avatar()
        .small())
        .transform(new CircleTransformation())
        .into(activityImageView);

      activityTitleTextView.setVisibility(View.GONE);
      activitysubTitleTextView.setText(Html.fromHtml(ksString.format(categoryBackingString,
        "friend_name", activity.user().name(),
        "project_name", activity.project().name(),
        "creator_name", activity.project().creator().name())));
    } else if (activity.category().equals(Activity.CATEGORY_FOLLOW)) {
      Picasso.with(context).load(activity.user().avatar()
        .small())
        .transform(new CircleTransformation())
        .into(activityImageView);

      activityTitleTextView.setText(ksString.format(categoryFollowingString, "user_name", activity.user().name()));
      activitysubTitleTextView.setText(categoryFollowBackString);

      // temp until followable :
      activityClickArea.setBackgroundResource(0);
      activitysubTitleTextView.setVisibility(View.GONE);
      titleLayoutParams.topMargin = (int)(10 * Resources.getSystem().getDisplayMetrics().density);
      activityTitleTextView.setLayoutParams(titleLayoutParams);

    } else {
      Picasso.with(context)
        .load(activity.project().photo().little())
        .into(activityImageView);

      layoutParams = new LinearLayout.LayoutParams(context.getResources().getDimensionPixelSize(R.dimen.discovery_activity_photo_width),
          context.getResources().getDimensionPixelSize(R.dimen.discovery_activity_photo_height));
      activityImageView.setLayoutParams(layoutParams);

      activityTitleTextView.setVisibility(View.VISIBLE);
      activityTitleTextView.setText(activity.project().name());

      switch(activity.category()) {
        case Activity.CATEGORY_FAILURE:
          activitysubTitleTextView.setText(categoryFailureString);
          break;
        case Activity.CATEGORY_CANCELLATION:
          activitysubTitleTextView.setText(categoryCancellationString);
          break;
        case Activity.CATEGORY_LAUNCH:
          activitysubTitleTextView.setText(ksString.format(categoryLaunchString, "user_name", activity.user().name()));
          break;
        case Activity.CATEGORY_SUCCESS:
          activitysubTitleTextView.setText(categorySuccessString);
          break;
        case Activity.CATEGORY_UPDATE:
          activitysubTitleTextView.setText(ksString.format(categoryUpdateString,
            "update_number", String.valueOf(activity.update().sequence()),
            "update_title", activity.update().title()));
          break;
        default:
          activityTitleTextView.setVisibility(View.GONE);
          activitysubTitleTextView.setVisibility(View.GONE);
          activityImageView.setVisibility(View.GONE);
          break;
      }
    }
  }

  @OnClick(R.id.see_activity_button)
  protected void seeActivityOnClick() {
    delegate.discoveryActivityViewHolderSeeActivityClicked(this);
  }

  @OnClick(R.id.activity_click_area)
  protected void activityProjectOnClick() {
    if (activity.category().equals(Activity.CATEGORY_UPDATE)) {
      delegate.discoveryActivityViewHolderUpdateClicked(this, activity);
    } else if(activity.category().equals(Activity.CATEGORY_FOLLOW)) {
      // TODO: HOLLA BACK GIRL
    } else {
      delegate.discoveryActivityViewHolderProjectClicked(this, activity.project());
    }
  }
}
