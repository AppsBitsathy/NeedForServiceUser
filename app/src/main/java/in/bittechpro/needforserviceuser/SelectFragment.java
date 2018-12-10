package in.bittechpro.needforserviceuser;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelectFragment extends Fragment {


    public SelectFragment() {
        // Required empty public constructor
    }

    View view;
    FloatingActionButton btn_select;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select, container, false);

        btn_select = view.findViewById(R.id.submit_select);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),UserActivity.class));
            }
        });

        return view;
    }

}
