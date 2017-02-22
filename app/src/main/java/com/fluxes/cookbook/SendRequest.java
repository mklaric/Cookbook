package com.fluxes.cookbook;


import android.os.AsyncTask;


class SendRequest extends AsyncTask<Request, Void, Request> {
    protected Request doInBackground(Request... requests) {
        return new Request(
                requests[0].Url().toString(),
                requests[0].Method(),
                requests[0].JSON(),
                RequestSender.Send(requests[0])
        );
    }

    @Override
    protected void onPostExecute(Request result) {}
}

