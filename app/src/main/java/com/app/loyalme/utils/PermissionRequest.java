package com.app.loyalme.utils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dharmraj on 24/1/17.
 */

public class PermissionRequest {

    public static final String[] NEEDED_PREVIEW_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    public static final String[] NEEDED_EDITOR_PERMISSIONS = new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"};
    private static final Map<Integer, ResponseWrapper> map = new ConcurrentHashMap();

    public PermissionRequest() {
    }

    public static void getPermission(@NonNull ResultDelegator context, String permissions, @NonNull PermissionRequest.Response response) {
        getPermission(context, new String[]{permissions}, response);
    }

    public static boolean hasAllPermission(@NonNull Context context, @NonNull String[] permissions) {
        if(Build.VERSION.SDK_INT < 23) {
            return true;
        } else {
            String[] var2 = permissions;
            int var3 = permissions.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String permission = var2[var4];
                if(ContextCompat.checkSelfPermission(context, permission) != 0) {
                    return false;
                }
            }

            return true;
        }
    }

    public static void getPermission(@NonNull ResultDelegator delegator, @NonNull String[] permissions, @NonNull PermissionRequest.Response response) {
        if(Build.VERSION.SDK_INT < 23) {
            response.permissionGranted();
        } else {
            HashSet permissionSet = new HashSet();
            Context context = delegator.getContext();
            String[] id = permissions;
            int var6 = permissions.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                String permission = id[var7];
                if(ContextCompat.checkSelfPermission(context, permission) != 0) {
                    permissionSet.add(permission);
                }
            }

            if(permissionSet.size() > 0) {
                int var9;
                for(var9 = '꒷'; map.containsKey(Integer.valueOf(var9)); var9 = (int)Math.round(Math.random() * 2.147483647E9D)) {
                    ;
                }

                map.put(Integer.valueOf(var9), new PermissionRequest.ResponseWrapper(response, permissions));
                delegator.requestPermissions((String[])permissionSet.toArray(new String[permissionSet.size()]), var9);
            } else {
                response.permissionGranted();
            }
        }

    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.ResponseWrapper responseWrapper = (PermissionRequest.ResponseWrapper)map.remove(Integer.valueOf(requestCode));
        if(responseWrapper != null) {
            if(grantResults.length > 0 && grantResults[0] == 0) {
                responseWrapper.response.permissionGranted();
            } else {
                responseWrapper.response.permissionDenied();
            }

        }
    }

    private static class ResponseWrapper {
        final PermissionRequest.Response response;
        final String[] permission;

        public ResponseWrapper(PermissionRequest.Response response, String permission) {
            this.response = response;
            this.permission = new String[]{permission};
        }

        public ResponseWrapper(PermissionRequest.Response response, String[] permission) {
            this.response = response;
            this.permission = permission;
        }
    }

    public interface Response {

        void permissionGranted();
        void permissionDenied();
    }










    public static class ResultDelegator {
        private final Response callback;
        private Activity activity;
        private Fragment fragment;

        public ResultDelegator(Activity activity) {
            this.activity = activity;
            this.callback = activity instanceof Response?(Response)activity:null;
        }

        public ResultDelegator(Fragment fragment) {
            this.fragment = fragment;
            this.callback = (Response)((Response)(fragment instanceof Response?fragment:(fragment.getActivity() instanceof Response?fragment.getActivity():null)));
        }

        public void requestPermissions(@NonNull String[] permissions, int requestCode) {
            if(Build.VERSION.SDK_INT >= 23) {
                if(this.activity != null) {
                    this.activity.requestPermissions(permissions, requestCode);
                } else {
                    this.fragment.requestPermissions(permissions, requestCode);
                }
            }

        }

        public void startActivityForResult(Intent intent, int resultId) {
            if(this.activity != null) {
                this.activity.startActivityForResult(intent, resultId);
            } else {
                this.fragment.startActivityForResult(intent, resultId);
            }

        }

        public Object raw() {
            return this.activity != null?this.activity:this.fragment;
        }

        public void permissionGranted() {
            if(this.callback != null) {
                this.callback.permissionGranted();
            }

        }

        public void permissionDenied() {
            if(this.callback != null) {
                this.callback.permissionDenied();
            }

        }

        public Context getContext() {
            return this.activity == null?this.fragment.getActivity():this.activity;
        }
    }
}
