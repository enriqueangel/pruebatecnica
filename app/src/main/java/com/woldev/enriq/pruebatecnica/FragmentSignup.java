package com.woldev.enriq.pruebatecnica;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentSignup extends Fragment implements View.OnClickListener {

    TextInputLayout campoCorreo, campoContrasena, campoConfirmarContrasena;
    TextInputEditText correo, contrasena, confirmarContrasena;
    CheckBox terminos;
    RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        campoCorreo = view.findViewById(R.id.campoCorreo);
        campoContrasena = view.findViewById(R.id.campoContraseña);
        campoConfirmarContrasena = view.findViewById(R.id.campoConfirmarContraseña);
        correo = view.findViewById(R.id.correo);
        contrasena = view.findViewById(R.id.contrasena);
        confirmarContrasena = view.findViewById(R.id.confirmarContrasena);

        terminos = view.findViewById(R.id.terminos);
        terminos.setText(getText(R.string.terminos));

        requestQueue = Volley.newRequestQueue(getActivity());

        Button btnRegistro = view.findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(this);

        return  view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegistro:
                boolean validarCorreo = validarCorreo();
                boolean validarContrasena = validarContrasena();
                boolean validarConfirmar = validarConfirmarContrasena(validarContrasena);
                boolean valirdarTerminos = validarTerminos();

                if (validarCorreo && validarContrasena && validarConfirmar && valirdarTerminos){
                    peticionApi();
                }
                break;
            default:
                break;
        }
    }

    private boolean validarTerminos() {
        if (!terminos.isChecked()){
            Toast.makeText(getActivity(), "Debe aceptar los terminos", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private boolean validarConfirmarContrasena(boolean valor) {
        if (TextUtils.isEmpty(confirmarContrasena.getText())){
            campoConfirmarContrasena.setError("Ingrese confirmación de contraseña");
            return false;
        } else {
            campoConfirmarContrasena.setError(null);
            if (valor){
                if (!confirmarContrasena.getText().toString().equals(contrasena.getText().toString())){
                    campoContrasena.setError("Las contraseñas deben ser iguales");
                    campoConfirmarContrasena.setError("Las contraseñas deben ser iguales");
                    return false;
                } else {
                    campoContrasena.setError(null);
                    campoConfirmarContrasena.setError(null);
                    return true;
                }
            }
            return false;
        }
    }

    private boolean validarContrasena() {
        if (TextUtils.isEmpty(contrasena.getText())){
            campoContrasena.setError("Ingrese contraseña");
            return false;
        } else {
            campoContrasena.setError(null);
            return true;
        }
    }

    private boolean validarCorreo() {
        if (!Patterns.EMAIL_ADDRESS.matcher(correo.getText().toString()).matches()){
            campoCorreo.setError("Correo erroneo");
            return false;
        } else {
            campoCorreo.setError(null);
            return true;
        }
    }

    private void peticionApi() {
        String url = getString(R.string.url);
        url = url + "/users/signup/";

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", correo.getText().toString());
        params.put("password", contrasena.getText().toString());
        params.put("plan", "1");

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCanceledOnTouchOutside(false);

        progressDialog.show();

        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.dismiss();
                            if (response.has("token")){
                                String token = response.get("token").toString();
                                Log.i("token", token);
                                Toast.makeText(getActivity(), "Registro completado", Toast.LENGTH_SHORT).show();
                            } else {
                                if (response.has("error")) {
                                    String error = response.get("error").toString();
                                    Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            Log.e("Volley", "Invalid JSON Object.");
                            Toast.makeText(getActivity(), "Error desconocido.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e("Volley", error.toString());
                        Toast.makeText(getActivity(), "Error en la conexion.", Toast.LENGTH_SHORT).show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Api-Key", "5941ca06064a1f77ac36ea62740e28d4a66f61c1");
                return headers;
            }
        };

        requestQueue.add(arrReq);
    }
}
