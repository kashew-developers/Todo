package in.kashewdevelopers.todo.provider_classes;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    public static void setSortType(Context context, int sortType) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SORT, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(Constants.SORT_CODE, sortType);
        editor.apply();
    }

    public static int getSortType(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Constants.SORT, Context.MODE_PRIVATE);
        return sharedPref.getInt(Constants.SORT_CODE, Constants.SORT_OLD_EARLY);
    }

}
