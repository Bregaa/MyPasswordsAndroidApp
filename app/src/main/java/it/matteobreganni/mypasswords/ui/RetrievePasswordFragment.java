package it.matteobreganni.mypasswords.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import it.matteobreganni.mypasswords.R;

public class RetrievePasswordFragment extends Fragment {

    private TextView hiddenServiceName;
    private TextView textViewRetrievePasswordTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_retrieve_password, container, false);

        hiddenServiceName = rootView.findViewById(R.id.hiddenServiceName);
        textViewRetrievePasswordTitle = rootView.findViewById(R.id.textViewRetrievePasswordTitle);

        // Sets the service name from the data sent by the previous fragment
        Bundle bundle = getArguments();
        if (bundle != null) {
            String serviceName = bundle.getString("serviceName");
            if (serviceName != null) {
                hiddenServiceName.setText(serviceName);
                textViewRetrievePasswordTitle.setText("Get " + serviceName + "'s password");
            }else{
                Toast.makeText(rootView.getContext(), "Unexpected error getting the service name (1)", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            }
        }else{
            Toast.makeText(rootView.getContext(), "Unexpected error getting the service name (2)", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }


        return rootView;
    }
}