package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Martin on 2/29/16.
 */
public class CrimeListFragment extends Fragment{

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType){
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position){
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount(){
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private Crime mCrime;

            private TextView mTitleTextView;
            private TextView mDateTextView;
            private CheckBox mSolvedCheckBox;

        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        public CrimeHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            mDateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);

            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
            mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    mCrime.setSolved(isChecked);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);

        }
    }

    private RecyclerView mCrimeRecyclerView;
    private TextView mEmptyView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Button mAddNewCrimeButton;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mEmptyView = (TextView) view.findViewById(R.id.empty_view);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAddNewCrimeButton = (Button) view.findViewById(R.id.add_new_crime);
        mAddNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCrime();
            }
        });

        if (savedInstanceState != null){
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }



        updateUI();

        return view;
    }

    private void NewCrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                NewCrime();
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeSize = crimeLab.getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeSize, crimeSize);

        if(!mSubtitleVisible){
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI(){
        CrimeLab crimelab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimelab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else{
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        if(mAdapter.getItemCount()==0){
            mCrimeRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
            mAddNewCrimeButton.setVisibility(View.VISIBLE);
        }
        else{
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            mAddNewCrimeButton.setVisibility(View.GONE);
        }

        updateSubtitle();
    }

}
