package com.otentico.android.nfc;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import se.anyro.nfc_reader.R;

public class Utils {

    public static final String HOST = "http://otenti.co/";


    public static Address getAddress(GPSTracker gps, Activity activity) throws IOException {
        LocationManager locationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String bestProvider = locationManager.getBestProvider(criteria, true);
        if(bestProvider==null) {
            Log.d("addr", "bestProvider " + bestProvider);
            bestProvider = locationManager.getBestProvider(criteria, false);
            Log.d("addr", "bestProvider " + bestProvider);
            if(bestProvider==null) {
                return null;
            }

        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
        Log.d("addr", "lst loc" + lastKnownLocation);
        //Location lastKnownLocation = getLastKnownLocation(locationManager);
        if (lastKnownLocation == null) {
            if(gps!=null) {
                lastKnownLocation = gps.getLocation();
                Log.d("addr", "lst loc net" + lastKnownLocation);
                if (lastKnownLocation == null)
                    return null;
            }
        }
        Log.d("addr", "" + criteria);
        Log.d("addr", "" + bestProvider);
        Log.d("addr", "" + lastKnownLocation + "lat: " + lastKnownLocation.getLatitude() + "lon: " + lastKnownLocation.getLongitude());
        Geocoder geocoder = new Geocoder(activity, Locale.US);
        Log.d("addr", "locale: " + Locale.getDefault());
        List<Address> address = geocoder.getFromLocation(
                lastKnownLocation.getLatitude(),
                lastKnownLocation.getLongitude(), 1);
        if (address != null && !address.isEmpty()) {
            Log.d("addr", "locale: " + address.get(0));
            return address.get(0);
        }
        return null;
    }


    public static String getIdentity(Activity activity) {
        AccountManager manager = AccountManager.get(activity);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");

            if (parts.length > 1)
                return parts[0];
        }
        return null;
    }

    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String dumpTagData(Parcelable p) {
        StringBuilder sb = new StringBuilder();
        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
        sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
        sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

        Log.d("nfc-reader", "HEX: " + getHex(id).replace(' ', ':') + " DEC: "
                + getDec(id));

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                MifareClassic mifareTag = MifareClassic.get(tag);
                String type = "Unknown";
                switch (mifareTag.getType()) {
                    case MifareClassic.TYPE_CLASSIC:
                        type = "Classic";
                        break;
                    case MifareClassic.TYPE_PLUS:
                        type = "Plus";
                        break;
                    case MifareClassic.TYPE_PRO:
                        type = "Pro";
                        break;
                }
                sb.append("Mifare Classic type: ");
                sb.append(type);
                sb.append('\n');

                sb.append("Mifare size: ");
                sb.append(mifareTag.getSize() + " bytes");
                sb.append('\n');

                sb.append("Mifare sectors: ");
                sb.append(mifareTag.getSectorCount());
                sb.append('\n');

                sb.append("Mifare blocks: ");
                sb.append(mifareTag.getBlockCount());
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        // return sb.toString();
        return getHex(id);
    }


    private static String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private static long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private static long getReversed(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    public static void playAudioTrack(final Context ctx, final int audioResId, final int channel_out) {
        new AsyncTask<Void, Void, Void>() {
            AudioTrack mAudioTrack;
            InputStream in1;

            @Override
            protected Void doInBackground(Void... params) {
                int buffMin = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                        channel_out, AudioFormat.ENCODING_PCM_16BIT,
                        buffMin, AudioTrack.MODE_STREAM);
                in1 = ctx.getResources().openRawResource(audioResId);
                byte[] music = null;
                try {
                    music = convertStreamToByteArray(in1);
                    if(Build.VERSION.SDK_INT<21)
                        mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(),AudioTrack.getMaxVolume());
                    else
                        mAudioTrack.setVolume(AudioTrack.getMaxVolume());
                    mAudioTrack.play();
                    mAudioTrack.write(music, 0, music.length);
                    //mAudioTrack.flush();
                    //in1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                try {
                    mAudioTrack.release();
                    in1.close();
                    mAudioTrack = null;
                } catch (IOException e) {

                    Log.e("AUDIO","onPostExecute "+ e.getMessage());
                }

            }
        }.execute();

    }

    private static byte[] convertStreamToByteArray(InputStream is) throws IOException {
        Log.d("AUDIO",""+is.available());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[is.available()];
        int i;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            baos.write(buff, 0, i);
        }

        baos.flush();
        byte[] b = baos.toByteArray();
        baos.close();
        return b; // be sure to close InputStream in calling function

    }

    //some short wav files has bad headers or something so this method skips the header (track.setPlaybackHeadPosition(100))
    public static void playMonoSoundWithBrokenFileHeader(Context ctx, int res, int channel_out){
        try{
            long totalAudioLen = 0;
            InputStream inputStream = ctx.getResources().openRawResource(res); // open the file
            totalAudioLen = inputStream.available();
            byte[] rawBytes = new byte[(int)totalAudioLen];
            AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                    44100,
                    channel_out,
                    AudioFormat.ENCODING_PCM_16BIT,
                    (int)totalAudioLen,
                    AudioTrack.MODE_STATIC);
            int offset = 0;
            int numRead = 0;

            while (offset < rawBytes.length
                    && (numRead=inputStream.read(rawBytes, offset, rawBytes.length-offset)) >= 0) {
                offset += numRead;
            } //don't really know why it works, it reads the file

            track.write(rawBytes, 0, (int)totalAudioLen); //write it in the buffer?
            track.play();
            track.pause();
            track.setPlaybackHeadPosition(100); // IMPORTANT to skip the click
            track.setPlaybackRate(44100);
            track.play();  // launch the play


            inputStream.close();
        }
        catch (FileNotFoundException e) {

            Log.e("HUY", "Error loading audio to bytes", e);
        } catch (IOException e) {
            Log.e("HUY", "Error loading audio to bytes", e);
        } catch (IllegalArgumentException e) {
            Log.e("HUY", "Error loading audio to bytes", e);
        }
    }

}
