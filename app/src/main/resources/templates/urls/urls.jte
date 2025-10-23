@import hexlet.code.model.Url
@import hexlet.code.dto.UrlsPage
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
<div class="container-fluid h-100 w-100 d-flex flex-column align-items-center justify-content-center">
    <section class="p-3 w-100">
        <table class="table table-bordered bg-transparent table-responsive">
            <caption class="visually-hidden">Список добавленных URL</caption>

            <thead>
                <tr>
                    <th class="bg-secondary text-light">ID</th>
                    <th class="bg-secondary text-light">URL</th>
                    <th class="bg-secondary text-light">Последняя проверка</th>
                    <th class="bg-secondary text-light">Код ответа</th>
                </tr>
            </thead>

            <tbody>
                @for(Url url : page.getUrls())
                    <tr>
                        <td class="bg-light">${url.getId()}</td>
                        <td class="bg-light"><a href="${url.getName()}">${url.getName()}</a></td>
                        <td class="bg-light"></td>
                        <td class="bg-light"></td>
                    </tr>
                @endfor
            </tbody>
        </table>
    </section>
</div>
`)