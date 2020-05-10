fun main(args: Array<String>) {
    val receipt = getReceipt()
    println(receipt)
    val result = when (receipt == expectedReceipt) {
        true -> "正确 ✅"
        false -> "错误 ❌"
    }
    println("\n结果：${result}")
}

interface Promotion {
    var barcodes: List<String>
}

data class BuyTwoGetOneFreePromotion(override var barcodes: List<String>) : Promotion {

}

fun loadPromotions(): List<Promotion> =
    listOf(BuyTwoGetOneFreePromotion(listOf("ITEM000000", "ITEM000001", "ITEM000005")))

data class Item(val barcode: String, val name: String, val unit: String, val price: Double) {

}

fun loadAllItems(): List<Item> {
    return listOf(
        Item("ITEM000000", "可口可乐", "瓶", 3.00),
        Item("ITEM000001", "雪碧", "瓶", 3.00),
        Item("ITEM000002", "苹果", "斤", 5.50),
        Item("ITEM000003", "荔枝", "斤", 15.00),
        Item("ITEM000004", "电池", "个", 2.00),
        Item("ITEM000005", "方便面", "袋", 4.50)
    )
}

val purchasedBarcodes = listOf(
    "ITEM000001",
    "ITEM000001",
    "ITEM000001",
    "ITEM000001",
    "ITEM000001",
    "ITEM000003-2",
    "ITEM000005",
    "ITEM000005",
    "ITEM000005"
)

fun geFreeCount(num: Int): Int {
    return (num - num % promotionNum) / promotionNum
}

fun isInPromotions(barcode: String): Boolean {
    return loadPromotions()[0].barcodes.contains(barcode)
}

fun getItemCount(barcode: String): Int {
    val decoupledCodeAndCount = barcode.split('-')
    return if (decoupledCodeAndCount.size > 1) decoupledCodeAndCount[1].toInt() else 1
}

fun getItemCode(barcode: String): String {
    return barcode.split('-')[0]
}

fun getPurchasedItems(purchasedBarcodes: List<String>): LinkedHashMap<String, Int> {
    val items = linkedMapOf<String, Int>()
    purchasedBarcodes.forEach {
        val itemCount = getItemCount(it)
        val itemCode = getItemCode(it)
        if (items[itemCode] == null) {
            items[itemCode] = itemCount
        } else {
            items[itemCode] = itemCount + items[itemCode]!!
        }
    }
    return items
}

fun formatNumber(num: Double): String {
    return String.format("%.2f", num)
}

fun getReceipt(): String {
    val purchasedItems = getPurchasedItems(purchasedBarcodes)
    var totalPaid: Double = 0.0
    var totalDiscount: Double = 0.0
    var receipt = """
***<没钱赚商店>收据***"""

    for (barcode in purchasedItems.keys) {
        val item = loadAllItems().find { it -> it.barcode == barcode }!!
        val count = purchasedItems[barcode]!!
        val totalPrice = item.price * count
        val discount = if (isInPromotions(item.barcode)) item.price * geFreeCount(count) else 0.0
        totalPaid = totalPaid + totalPrice - discount
        totalDiscount += discount

        receipt += "\n名称：${item.name}，数量：${count}${item.unit}，单价：${formatNumber(item.price)}(元)，" +
                "小计：${totalPrice - discount}(元)"

    }

    receipt += """
----------------------
总计：${formatNumber(totalPaid)}(元)
节省：${formatNumber(totalDiscount)}(元)
**********************
"""

    return receipt
}

const val expectedReceipt = """
***<没钱赚商店>收据***
名称：雪碧，数量：5瓶，单价：3.00(元)，小计：12.0(元)
名称：荔枝，数量：2斤，单价：15.00(元)，小计：30.0(元)
名称：方便面，数量：3袋，单价：4.50(元)，小计：9.0(元)
----------------------
总计：51.00(元)
节省：7.50(元)
**********************
"""

const val promotionNum = 3