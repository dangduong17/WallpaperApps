package pion.tech.pionbase.data.model.wallpaper

data class CategoryUIModel(
    val title: String = "",
    val imageUrl: String = ""
) {
    val resolvedThumbnailUrl: String
        get() {
            return when {
                imageUrl.contains("/800/1200") -> imageUrl.replace("/800/1200", "/300/450")
                imageUrl.contains("w=800&h=1200") -> imageUrl.replace("w=800&h=1200", "w=300&h=450")
                else -> imageUrl
            }
        }
}
