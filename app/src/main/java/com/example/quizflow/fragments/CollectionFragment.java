package com.example.quizflow.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quizflow.R;
import com.example.quizflow.Retrofit2Client;
import com.example.quizflow.activities.QuizEditor2Activity;
import com.example.quizflow.adapters.CollectionAdapter;
import com.example.quizflow.models.QuizEditorModel;
import com.example.quizflow.models.QuizModel;
import com.example.quizflow.utils.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CollectionFragment extends Fragment {
    private RecyclerView recyHistory, recyCreated;
    private TextView txt_noHist, txt_noCrea;

    private CollectionAdapter historyAdapter, createdAdapter;
    private Retrofit2Client ret2 = new Retrofit2Client();
    private List<QuizModel> attemptedList = new ArrayList<>();
    private List<QuizModel> createdList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        historyAdapter = new CollectionAdapter(getContext(), attemptedList);
        recyHistory = view.findViewById(R.id.recy_history);
        recyHistory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyHistory.setAdapter(historyAdapter);

        createdAdapter = new CollectionAdapter(getContext(), createdList);
        recyCreated = view.findViewById(R.id.recy_created);
        recyCreated.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyCreated.setAdapter(createdAdapter);


        getData();

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getData();
        }
    }

    private void getData() {
       ret2.getAPI().getQuizzesAttemptedByUid(Utilities.getUID(requireContext()))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<List<QuizModel>> call, Response<List<QuizModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            attemptedList = response.body();

                            requireActivity().runOnUiThread(() -> loadAttempted());
                        } else {
                            String msg = "Unknown error";
                            if (response.errorBody() != null) {
                                try {
                                    msg = response.errorBody().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            Utilities.showError(requireActivity(), "QF_GET_HIST", "Error: " + msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<QuizModel>> call, Throwable t) {
                        Utilities.showError(requireActivity(), "QF_GET_HIST", "Failure: " + t.getMessage());
                    }
                });

       ret2.getAPI().getQuizzesCreatedByUid(Utilities.getUID(requireContext())).enqueue(new Callback<>() {
           @Override
           public void onResponse(Call<List<QuizModel>> call, Response<List<QuizModel>> response) {
               if (response.isSuccessful() && response.body() != null) {
                   createdList = response.body();

                   requireActivity().runOnUiThread(() -> loadCreated());
               } else {
                   String msg = "Unknown error";
                   if (response.errorBody() != null) {
                       try {
                           msg = response.errorBody().string();
                       } catch (IOException e) {
                           throw new RuntimeException(e);
                       }
                   }
                   Utilities.showError(requireActivity(), "QF_GET_CREATED", "Error: " + msg);
               }
           }

           @Override
           public void onFailure(Call<List<QuizModel>> call, Throwable t) {
               Utilities.showError(requireActivity(), "QF_GET_CREATED", "Failure: " + t.getMessage());
           }
       });
    }

    private void loadAttempted() {
        txt_noHist = requireActivity().findViewById(R.id.txt_noHist);
        if (attemptedList == null || attemptedList.isEmpty()) {
            txt_noHist.setVisibility(View.VISIBLE);
        } else {
            txt_noHist.setVisibility(View.GONE);
        }
        historyAdapter.setData(attemptedList);
    }

    private void loadCreated() {
        txt_noCrea = requireActivity().findViewById(R.id.txt_noCrea);
        if (createdList == null || createdList.isEmpty()) {
            txt_noCrea.setVisibility(View.VISIBLE);
        } else {
            txt_noCrea.setVisibility(View.GONE);
        }
        createdAdapter.setData(createdList);
    }
}