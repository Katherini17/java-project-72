@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter
@import hexlet.code.dto.urls.UrlPage
@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <section class="w-100 mt-3">
            <h1>${page.getUrl().getName()}</h1>
            <ul  class="text-secondary list-unstyled">
                <li>
                    <span>Дата создания:</span>
                    <span> ${
                            page.getUrl()
                                .getCreatedAt()
                                .toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                        }</span>
                </li>
                <li>ID: ${page.getUrl().getId()}</li>
            </ul>
    </section>
    <section class="w-100">
        <div>
            <form action="${NamedRoutes.urlCheckPath(page.getUrl().getId())}" method="post">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>
            <h2>Результаты проверок:</h2>
        </div>

        <div class="table-responsive">
            <table class="table table-bordered bg-transparent">
                <caption class="visually-hidden">Список добавленных URL</caption>

                <thead>
                <tr>
                    <th class="bg-light">ID</th>
                    <th class="bg-light">Код ответа</th>
                    <th class="bg-light">title</th>
                    <th class="bg-light">h1</th>
                    <th class="bg-light">description</th>
                    <th class="bg-light">Дата проверки</th>
                </tr>
                </thead>

                <tbody>
                @for(UrlCheck urlCheck : page.getUrlChecks())
                    <tr>
                        <td class="bg-light">${urlCheck.getId()}</td>
                        <td class="bg-light">Код ответа</td>
                        <td class="bg-light">title</td>
                        <td class="bg-light">h1</td>
                        <td class="bg-light">description</td>
                        <td class="bg-light">${
                            urlCheck.getCreatedAt()
                                .toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                            }
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </div>
    </section>
`)