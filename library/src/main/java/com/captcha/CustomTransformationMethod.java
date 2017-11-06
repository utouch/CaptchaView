package com.captcha;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by zs on 17/11/3.
 * <p>
 * Description
 */
public class CustomTransformationMethod extends PasswordTransformationMethod {
    String transformation;

    public CustomTransformationMethod(String transformation) {
        this.transformation = transformation;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source;
        }

        @Override
        public int length() {
            return mSource.length();
        }

        @Override
        public char charAt(int index) {
            return transformation.charAt(0);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end);
        }
    }

}