package com.stronglifters.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksView;

public class MainActivity extends AppCompatActivity implements TurbolinksAdapter {
  private static final String BASE_URL = "https://www.stronglifters.com";
  private static final String INTENT_URL = "intentUrl";

  private String location;
  private TurbolinksView turbolinksView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

    // this for debug builds of your app (it is off by default)
    TurbolinksSession.getDefault(this).setDebugLoggingEnabled(true);

    location = loadLocation();

    TurbolinksSession.getDefault(this)
      .activity(this)
      .adapter(this)
      .view(turbolinksView)
      .visit(location);
  }

  private String loadLocation(){
    String intent = getIntent().getStringExtra(INTENT_URL);
    return intent == null ? BASE_URL : intent;
  }

  @Override
  protected void onRestart() {
    super.onRestart();

    TurbolinksSession.getDefault(this)
      .activity(this)
      .adapter(this)
      .restoreWithCachedSnapshot(true)
      .view(turbolinksView)
      .visit(location);
  }

  @Override
  public void onPageFinished() { }

  @Override
  public void onReceivedError(int errorCode) {
    handleError(errorCode);
  }

  @Override
  public void pageInvalidated() { }

  @Override
  public void requestFailedWithStatusCode(int statusCode) {
    handleError(statusCode);
  }

  @Override
  public void visitCompleted() { }

  @Override
  public void visitProposedToLocationWithAction(String location, String action) {
    Intent intent = new Intent(this, MainActivity.class);
    intent.putExtra(INTENT_URL, location);
    this.startActivity(intent);
  }

  private void handleError(int code) {
    if (code == 404) {
      TurbolinksSession.getDefault(this)
        .activity(this)
        .adapter(this)
        .restoreWithCachedSnapshot(false)
        .view(turbolinksView)
        .visit(BASE_URL + "/404");
    }
    else {
      TurbolinksSession.getDefault(this)
        .activity(this)
        .adapter(this)
        .restoreWithCachedSnapshot(false)
        .view(turbolinksView)
        .visit(BASE_URL + "/500");
    }
  }
}
