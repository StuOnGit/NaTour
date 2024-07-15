package com.example.natour.ui.account.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.natour.controllers.ControllerAccount;
import com.example.natour.ui.account.fragmentAccount.CompilationFragment;
import com.example.natour.ui.account.fragmentAccount.PathFragment;
import com.example.natour.ui.account.fragmentAccount.PhotoFragment;
import com.example.natour.ui.account.fragmentAccount.ReviewFragment;

public class AdapterPathFragment extends FragmentStateAdapter {
    Bundle bundle;
    public AdapterPathFragment(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ControllerAccount controllerAccount) {
        super(fragmentManager, lifecycle);
        setUpFragments(controllerAccount);
    }

    private void setUpFragments(ControllerAccount controllerAccount){
        bundle = new Bundle();
        bundle.putParcelable("User", controllerAccount.getUser());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if(position == 0){
            fragment = new PathFragment();
            fragment.setArguments(bundle);
        }
        else if(position == 1){
            fragment = new PhotoFragment();
            fragment.setArguments(bundle);
        }else if(position == 2){
            fragment = new CompilationFragment();
            fragment.setArguments(bundle);

        }else{
            fragment = new ReviewFragment();
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }

}
