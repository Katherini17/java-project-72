@import hexlet.code.model.Url
@import hexlet.code.dto.UrlsPage
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
<div class="container-fluid h-100 w-100 d-flex flex-column align-items-center justify-content-center">
    <section class="p-3 w-100">
        @for(Url url : page.getUrls())
            <p><a href="${url.getName()}">${url.getName()}</a></p>
            <br>
        @endfor
    </section>
</div>
`)