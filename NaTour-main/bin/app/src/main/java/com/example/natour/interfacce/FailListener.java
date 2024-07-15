package com.example.natour.interfacce;

public interface FailListener {
    void onAuthFail(String msg);
    void onDBFail(String msg);
    void onLocalProblem(String msg);
    void onJSONException(String msg);
    void onHTTPError(String msg);
}
