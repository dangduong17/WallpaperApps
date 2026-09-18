package pion.tech.pionbase.data.model.wallpaper

data class CategoryDtoModel(
    val title: String,
    val imageUrl: String
)

fun CategoryDtoModel.toPresentation() = CategoryUIModel(
    title = this.title,
    imageUrl = this.imageUrl
)

fun CategoryDtoModel.toEntity() = CategoryEntity(
    title = this.title,
    imageUrl = this.imageUrl
)
