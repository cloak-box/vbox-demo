package com.black.cat.system.demo.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.black.cat.system.demo.R;

public class VSearchView extends LinearLayout {
  private SearchViewListener mSearchViewListener;
  private EditText editText;

  public VSearchView(Context context) {
    this(context, null);
  }

  public VSearchView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public VSearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    this(context, attrs, defStyleAttr, 0);
  }

  public VSearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    inflate(getContext(), R.layout.app_search_view, this);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    editText = findViewById(R.id.edit_search);
    editText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            if (mSearchViewListener != null) {
              String str = s.toString();
              mSearchViewListener.onTextChange(str);
            }
          }
        });
    setUpTextView(editText);
    TextView textView = findViewById(R.id.tv_cancel);
    textView.setOnClickListener(v -> dismiss());
    setUpTextView(textView);
  }

  public void dismiss() {
    if (mSearchViewListener != null) {
      mSearchViewListener.onDismiss();
    }
  }

  public void setSearchViewListener(SearchViewListener mSearchViewListener) {
    this.mSearchViewListener = mSearchViewListener;
  }

  private void setUpTextView(TextView textView) {
    textView.setTextSize(16f);
    //    textView.setTextColor(getResources().getColor(R.color.common_title_color_default));
    textView.setMaxLines(1);
    textView.setEllipsize(TextUtils.TruncateAt.END);
    textView.setGravity(Gravity.LEFT | Gravity.CENTER);
  }

  public EditText getEditText() {
    return editText;
  }

  public interface SearchViewListener {
    void onDismiss();

    void onTextChange(String txt);
  }
}
