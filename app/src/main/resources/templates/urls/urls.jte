@import hexlet.code.model.Url
@import hexlet.code.dto.UrlsPage
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
<div class="container-fluid h-100 w-100 d-flex flex-column align-items-center justify-content-center">
    <section class="p-3 w-100">
        <div class="table-responsive">
                <table class="table table-bordered bg-transparent">
                <caption class="visually-hidden">Список добавленных URL</caption>

                <thead>
                <tr>
                    <th class="bg-light text-light">ID</th>
                    <th class="bg-light text-light">URL</th>
                    <th class="bg-light text-light">Последняя проверка</th>
                    <th class="bg-light text-light">Код ответа</th>
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
        </div>

    </section>
</div>
`)