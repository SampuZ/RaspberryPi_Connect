package shompu.rasberrypi_connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.InputStream;

/**
 * Created by Sampu on 07-07-2019.
 */

public class SSHConnectionManager {

    private static SSHConnectionManager sshConnectionManager;
    private Activity mActivity;

    public static SSHConnectionManager getInstance(Activity activity) {
        if (sshConnectionManager == null) {
            sshConnectionManager = new SSHConnectionManager(activity);
        }
        return sshConnectionManager;
    }

    private SSHConnectionManager(Activity activity) {
        mActivity = activity;
    }

    private String user = "pi";
    private String password = "XXXXXXX";
    private String host = "PiBox";
    private int port = 22;

    public void executeSSHcommand(String command) {
        new SSHCommand().execute(command);
    }

    private class SSHCommand extends AsyncTask<String, Void, String> {

        Session session;
        ChannelExec channel;
        JSch jsch;

        @Override
        protected String doInBackground(String... strings) {
            try {
                jsch = new JSch();
                session = jsch.getSession(user, host, port);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setTimeout(10000);
                session.connect();
                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(strings[0]);
                channel.connect();

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Success", Toast.LENGTH_SHORT).show();
                    }
                });
                return readChannelOutput(channel).toString();
            } catch (final JSchException e) {
                // show the error in the UI
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Failure. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(final String output) {
            super.onPostExecute(output);
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (output != null) {
                        //Toast.makeText(mActivity, "Output = " + output, Toast.LENGTH_SHORT).show();
//                        new AlertDialog.Builder(mActivity).setMessage(output)
////                                .setNegativeButton("OK", null)
////                                .setTitle("Output")
////                                .show();
                        ((MainActivity)mActivity).textView.setText(output);

                    } else
                        Toast.makeText(mActivity, "No Output", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private StringBuilder readChannelOutput(ChannelExec channel) {

        byte[] buffer = new byte[1024];
        StringBuilder result = new StringBuilder();

        try {
            InputStream in = channel.getInputStream();
            String line = "";
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    line = new String(buffer, 0, i);
                    System.out.println(line);
                    result.append(line);
                }

                if (line.contains("logout")) {
                    break;
                }

                if (channel.isClosed()) {
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
        } catch (Exception e) {
            System.out.println("Error while reading channel output: " + e);
        }
        return result;
    }
}
