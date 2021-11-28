package info.hououji.lokyin2021;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateFragment extends Fragment {



    public DateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DateFragment newInstance() {
        DateFragment fragment = new DateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    TimePicker timePicker ;
    DatePicker datePicker ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_date, container, false);
        timePicker = view.findViewById(R.id.timePicker);
        datePicker = view.findViewById(R.id.datePicker);

        timePicker.setIs24HourView(true);

        Calendar c = Calendar.getInstance();
        c.setTime(Common.getCurrentDate());

        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Common.getCurrentDate().setTime(getDate().getTime());
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Common.getCurrentDate().setTime(getDate().getTime());
            }
        });

        setDate(Common.getCurrentDate());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        setDate(Common.getCurrentDate());

    }

    private void setDate(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        timePicker.setHour(c.get(Calendar.HOUR_OF_DAY));
        timePicker.setMinute(c.get(Calendar.MINUTE));
        datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
    }

    private Date getDate(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, datePicker.getYear());
        c.set(Calendar.MONTH, datePicker.getMonth()) ;
        c.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth()) ;
        c.set(Calendar.HOUR_OF_DAY, timePicker.getHour()) ;
        c.set(Calendar.MINUTE, timePicker.getMinute()) ;
        c.set(Calendar.SECOND, 0) ;
        return c.getTime() ;
    }
}