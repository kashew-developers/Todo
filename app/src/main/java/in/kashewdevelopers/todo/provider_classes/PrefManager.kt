package `in`.kashewdevelopers.todo.provider_classes

import android.content.Context

class PrefManager {

    companion object {
        fun setSortType(context: Context, sortType: Int) {
            val sharedPref = context.getSharedPreferences(Constants.SORT, Context.MODE_PRIVATE)

            val editor = sharedPref.edit()

            editor.putInt(Constants.SORT_CODE, sortType)
            editor.apply()
        }

        fun getSortType(context: Context): Int {
            val sharedPref = context.getSharedPreferences(Constants.SORT, Context.MODE_PRIVATE)
            return sharedPref.getInt(Constants.SORT_CODE, Constants.SORT_OLD_EARLY)
        }
    }

}
