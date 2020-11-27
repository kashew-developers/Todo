package `in`.kashewdevelopers.todo.provider_classes

class Constants {

    companion object {
        const val ACTIVITY_ADD_TASK = 101
        const val ACTIVITY_EDIT_TASK = 102
        const val ACTIVITY_TASK_LIST = 103

        // sort types
        const val SORT = "sort"
        const val SORT_CODE = "sortType"
        const val SORT_A_Z = 0
        const val SORT_Z_A = 1
        const val SORT_OLD_EARLY = 2
        const val SORT_EARLY_OLD = 3
        const val SORT_COMPLETE_PENDING = 4
        const val SORT_PENDING_COMPLETE = 5

        // intent data
        const val GROUP = "groupName"
    }
}
