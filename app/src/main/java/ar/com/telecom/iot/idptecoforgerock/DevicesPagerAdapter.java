package ar.com.telecom.iot.idptecoforgerock;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DevicesPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public DevicesPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        // Devuelve el fragmento en la posición especificada
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        // Devuelve el número de fragmentos en la lista
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        // Agrega un fragmento y un título a la lista
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Devuelve el título del fragmento en la posición especificada
        return mFragmentTitleList.get(position);
    }
}
