package com.circlepix.android.interfaces;

/**
 * Created by relly on 4/16/15.
 */
public interface IBaseActionBarCallback {

    void back();
    void logout();
    void add();

    class Null implements IBaseActionBarCallback {

        @Override
        public void back() {

        }

        @Override
        public void logout() {

        }

        @Override
        public void add() {

        }
    }

}

