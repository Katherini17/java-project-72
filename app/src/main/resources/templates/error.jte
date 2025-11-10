@import hexlet.code.dto.ErrorPage
@param ErrorPage page

@template.layout.page(
page = page,
content = @`
<section class="w-100">
    <h1>Похоже, возникла ошибка :(</h1>
    <h2>${page.getStatusCode()}</h2>
    <p>${page.getErrorMessage()}</p>
</section>
`)