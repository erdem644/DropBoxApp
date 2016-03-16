package com.example.cip_a.dropboxapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import javax.activation.DataHandler;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;


public class MainActivity extends AppCompatActivity {

    private Button btnSend;
    private Button btnGet;
    private String subject="Deneme";
    private String messageBody="Kamera açıp foto çekip ekleyip yollaya bildim sonunda.";
    private InternetAddress[] emails = new  InternetAddress[2];
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private Gmail myGmail;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = (Button)findViewById(R.id.button);
        btnSend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    emails[0] = new InternetAddress("kerimodabas@gmail.com", "kerim");
                    emails[1] = new InternetAddress("eszwahli@gmail.com", "kerim11");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                myGmail = new Gmail();

                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                        REQUEST_CODE_CAPTURE_IMAGE);
            }
        });

        btnGet = (Button)findViewById(R.id.button2);
        btnGet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                myGmail = new Gmail();
                Session sessionGet = myGmail.createSessionObjectForGet();
                new GetMailTask().execute(sessionGet);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            DataHandler handlerPhoto = new DataHandler(new ByteArrayDataSource(byteArray, "image/png"));

            Session session = myGmail.createSessionObjectForSend();

            try {
                Message message = myGmail.createMessage(emails, subject, messageBody, session, handlerPhoto);
                new SendMailTask().execute(message);
            } catch (AddressException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

    }

    public class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;
 
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending information", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class GetMailTask extends AsyncTask<Session, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending information", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Session... session) {
            try {
                myGmail.getMessage(session[0]);
            } catch (AddressException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
