package com.gustavok.peach;

interface SenatorsCallbackInterface {
    void onProgress(long bytesWritten, long totalSize);
    void onSuccess(Senator[] senators);
}
