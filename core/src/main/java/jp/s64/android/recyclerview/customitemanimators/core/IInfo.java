package jp.s64.android.recyclerview.customitemanimators.core;

import android.support.annotation.NonNull;

public interface IInfo {

    enum Type {
        Addition,
        Change,
        Move,
        Remove,
        ;
    }

    @NonNull
    Type getType();

}
