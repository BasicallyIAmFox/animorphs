package basicallyiamfox.ani.util

enum class ComparisonOperator {
    equal {
        override fun proceed(left: Number, right: Number): Boolean = left == right
    },
    unequal {
        override fun proceed(left: Number, right: Number): Boolean = left != right
    },
    greater {
        override fun proceed(left: Number, right: Number): Boolean = left.toDouble() > right.toDouble()
    },
    greaterOrEqual {
        override fun proceed(left: Number, right: Number): Boolean = left.toDouble() >= right.toDouble()
    },
    less {
        override fun proceed(left: Number, right: Number): Boolean = left.toDouble() < right.toDouble()
    },
    lessOrEqual {
        override fun proceed(left: Number, right: Number): Boolean = left.toDouble() <= right.toDouble()
    };

    abstract fun proceed(left: Number, right: Number): Boolean
}