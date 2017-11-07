package com.captcha;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by zs on 17/11/3.
 * <p>
 * Description
 */

public class CaptchaView extends RelativeLayout implements TextWatcher, InputEditText.OnDelKeyEventListener {
    private Context mContext;
    private String mPasswordTransformation;
    private OnInputCompleteCallback mOnInputCompleteCallback;
    private PasswordTransformationMethod mTransformationMethod;
    private int mFocusBackground;
    private int mUnFocusBackground;
    private int mInputType;
    private int mNumber;
    private ColorStateList mTextColor;
    private Boolean mHasFocusBackground;
    private int mTextSize;
    private int mTextViewHeight;
    private int mTextViewWidth;
    private int mDividerWidth;
    private TextView mArrayTextView[];
    private LinearLayout mParentView;
    private InputEditText mInputEditText;
    public static final int INPUT_TYPE_NUMBER = 0;
    public static final int INPUT_TYPE_TEXT = 1;
    public static final int INPUT_TYPE_NUMBER_TEXT = 2;

    public CaptchaView(Context context) {
        this(context, null);
    }

    public CaptchaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptchaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CaptchaView, defStyleAttr, 0);
        mNumber = a.getInt(R.styleable.CaptchaView_number, -1);
        mTextColor = a.getColorStateList(R.styleable.CaptchaView_android_textColor);
        if (mTextColor == null)
            mTextColor = ColorStateList.valueOf(getResources().getColor(android.R.color.primary_text_light));
        mTextSize = a.getDimensionPixelSize(R.styleable.CaptchaView_android_textSize, -1);
        if (mTextSize != -1)
            this.mTextSize = Util.px2sp(context, mTextSize);

        mHasFocusBackground = a.getBoolean(R.styleable.CaptchaView_hasFocusBackground, false);
        mTextViewWidth = a.getDimensionPixelSize(R.styleable.CaptchaView_captchaWidth, -1);
        mTextViewHeight = a.getDimensionPixelSize(R.styleable.CaptchaView_captchaHeight, -1);
        mDividerWidth = a.getDimensionPixelOffset(R.styleable.CaptchaView_dividerWidth, -1);
        if (mDividerWidth != -1) setDivideWidth(mDividerWidth);
        mFocusBackground = a.getResourceId(R.styleable.CaptchaView_focusBackground, -1);
        mUnFocusBackground = a.getResourceId(R.styleable.CaptchaView_unFocusBackground, -1);
        int gravity = a.getInt(R.styleable.CaptchaView_android_gravity, -1);
        if (gravity != -1) setGravity(gravity);
        mInputType = a.getInt(R.styleable.CaptchaView_inputType, -1);
        if (mInputType != -1)
            setInputType(mInputType, null);
        mPasswordTransformation = a.getString(R.styleable.CaptchaView_passwordTransformation);
        if (mPasswordTransformation != null)
            mTransformationMethod = new CustomTransformationMethod(mPasswordTransformation);
        a.recycle();
    }

    private void init(Context context) {
        mContext = context;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mParentView = new LinearLayout(context);
        mParentView.setLayoutParams(params);
        mParentView.setOrientation(LinearLayout.HORIZONTAL);
        mParentView.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        addView(mParentView);
        mInputEditText = new InputEditText(context);
        mInputEditText.setLayoutParams(params);
        mInputEditText.setCursorVisible(false);
        mInputEditText.addTextChangedListener(this);
        mInputEditText.setOnDelKeyEventListener(this);
        mInputEditText.setTextColor(getResources().getColor(android.R.color.transparent));
        mInputEditText.setBackgroundResource(android.R.color.transparent);
        addView(mInputEditText);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mParentView.post(new Runnable() {
            @Override
            public void run() {
                inflates();
            }
        });
    }

    private void inflates() {
        if (mNumber != -1) {
            mParentView.removeAllViews();
            mArrayTextView = new TextView[mNumber];
            int measuredWidth = mParentView.getMeasuredWidth();
            int rectSize = (measuredWidth - (mDividerWidth * (mNumber - 1))) / mNumber;
            for (int i = 0; i < mNumber; i++) {
                final TextView textView = new TextView(mContext);
                // 判断如果没有设置宽高，则根据控件宽度均分
                if (mTextViewHeight != -1 && mTextViewWidth != -1) {
                    textView.setWidth(mTextViewWidth);
                    textView.setHeight(mTextViewHeight);
                } else {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                      rectSize, rectSize, 1f);
                    textView.setLayoutParams(lp);
                }
                if (mTextSize != -1)
                    textView.setTextSize(mTextSize);
                if (mTextColor != null)
                    textView.setTextColor(mTextColor);
                if (mFocusBackground != -1 && mUnFocusBackground != -1 && mHasFocusBackground)
                    textView.setBackgroundResource(i != 0 ? mUnFocusBackground : mFocusBackground);
                else
                    textView.setBackgroundResource(mUnFocusBackground != -1 ? mUnFocusBackground : R.drawable.bg_edit_un_focus);
                textView.setGravity(Gravity.CENTER);
                textView.setFocusable(false);
                setTransformation(textView);
                mArrayTextView[i] = textView;
                mParentView.addView(textView);
            }
            mParentView.post(new Runnable() {
                @Override
                public void run() {
                    mInputEditText.setHeight(mParentView.getMeasuredHeight());
                }
            });
        }

    }

    /**
     * 设置输入类型
     *
     * @param inputType
     *          InputType.TYPE_CLASS_NUMBER
     * @param digits
     *          filter
     */
    public void setInputType(int inputType, final String digits) {
        switch (inputType) {
            case INPUT_TYPE_NUMBER:
                mInputEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                break;
            case INPUT_TYPE_TEXT:
                mInputEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case INPUT_TYPE_NUMBER_TEXT:
                mInputEditText.setKeyListener(new NumberKeyListener() {

                    @Override
                    public int getInputType() {
                        return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                    }

                    protected char[] getAcceptedChars() {
                        char[] numberChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' };
                        if (!TextUtils.isEmpty(digits))
                            numberChars = digits.toCharArray();
                        return numberChars;
                    }

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        char[] accept = getAcceptedChars();

                        int i;
                        for (i = start; i < end; i++) {
                            if (!ok(accept, source.charAt(i))) {
                                if (mOnInputCompleteCallback != null)
                                    mOnInputCompleteCallback.onError(Character.toString(source.charAt(i)));
                                break;
                            }
                        }
                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                });
                break;
        }

    }


    /**
     * 设置间隔宽度
     *
     * @param dividerWidth 间距
     */
    public void setDivideWidth(int dividerWidth) {
        mParentView.setDividerDrawable(createDivideShape(dividerWidth));
    }

    /**
     * 设置输入框的摆放位置.
     *
     * @param gravity 请参阅 {@link android.view.Gravity}
     */
    public void setGravity(int gravity) {
        if (mParentView != null)
            mParentView.setGravity(gravity);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        setCaptcha(s.toString());
    }

    @Override
    public void onDeleteClick() {
        deleteCaptcha();
    }

    /**
     * 设置验证码
     *
     * @param captcha 验证码
     */
    private void setCaptcha(String captcha) {
        if (TextUtils.isEmpty(captcha)) return;
        for (int i = 0; i < mArrayTextView.length; i++) {
            TextView textView = mArrayTextView[i];
            if (TextUtils.isEmpty(textView.getText().toString())) {
                textView.setText(captcha);
                if (mUnFocusBackground != -1 && mFocusBackground != -1 && mHasFocusBackground) {
                    textView.setBackgroundResource(mUnFocusBackground);
                    if (i < mArrayTextView.length - 1)
                        mArrayTextView[i + 1].setBackgroundResource(mFocusBackground);
                }
                if (i == mArrayTextView.length - 1 && mOnInputCompleteCallback != null)
                    mOnInputCompleteCallback.onInputCompleteListener(getCaptcha());
                break;
            }
        }
        mInputEditText.setText("");
    }

    /**
     * 删除验证码
     */
    private void deleteCaptcha() {
        for (int i = mArrayTextView.length - 1; i >= 0; i--) {
            TextView textView = mArrayTextView[i];
            if (!TextUtils.isEmpty(textView.getText().toString())) {
                textView.setText("");
                if (mUnFocusBackground != -1 && mFocusBackground != -1 && mHasFocusBackground) {
                    textView.setBackgroundResource(mFocusBackground);
                    if (i < mArrayTextView.length - 1)
                        mArrayTextView[i + 1].setBackgroundResource(mUnFocusBackground);
                }
                break;
            }
        }
    }

    /**
     * 获取已经输入的验证码
     *
     * @return 验证码
     */
    public String getCaptcha() {
        StringBuilder sb = new StringBuilder();
        for (TextView textView : mArrayTextView) {
            sb.append(textView.getText().toString());
        }
        return sb.toString();
    }

    /**
     * 清空输入框
     */
    public void clear() {
        for (int i = 0; i < mArrayTextView.length; i++) {
            TextView textView = mArrayTextView[i];
            textView.setText("");
            textView.setBackgroundResource(i != 0 ? mUnFocusBackground : mFocusBackground);
        }
    }

    /**
     * 设置输入框数量.
     *
     * @param number 输入框数量
     */
    public void setNumber(int number) {
        if (mNumber != number) {
            mNumber = number;
            mInputEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mNumber)});
            onFinishInflate();
        }
    }

    private void setTransformation(TextView textView) {
        textView.setTransformationMethod(mTransformationMethod == null ?
                          HideReturnsTransformationMethod.getInstance() : mTransformationMethod);
    }

    private Drawable createDivideShape(int width) {
        GradientDrawable shape = new GradientDrawable();
        shape.setSize(width, 0);
        return shape;
    }


    public interface OnInputCompleteCallback {
        void onInputCompleteListener(String captcha);

        void onError(String error);
    }

    /**
     * 设置输入完成监听
     *
     * @param callback 回调
     */
    public void setOnInputCompleteListener(OnInputCompleteCallback callback) {
        this.mOnInputCompleteCallback = callback;
    }
}
