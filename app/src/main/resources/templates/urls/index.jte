
@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter
@import hexlet.code.dto.urls.UrlsPage

@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <section class="w-100">
        <div class="table-responsive">
                <table class="table table-bordered bg-transparent">
                <caption class="visually-hidden">Список добавленных URL</caption>

                <thead>
                    <tr>
                        <th class="bg-light">ID</th>
                        <th class="bg-light">URL</th>
                        <th class="bg-light">Последняя проверка</th>
                        <th class="bg-light">Код ответа</th>
                    </tr>
                </thead>

                <tbody>

                @for(Url url : page.getUrls())
                <tr>
                    <td class="bg-light">${url.getId()}</td>
                    <td class="bg-light"><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a></td>
                    !{UrlCheck lastUrlCheck = page.getLastUrlsChecks().get(url.getId());}
                    @if(lastUrlCheck != null)
                        <td class="bg-light">${
                            lastUrlCheck.getCreatedAt()
                                .toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))}</td>
                        <td class="bg-light">${lastUrlCheck.getStatusCode()}</td>
                    @else
                        <td class="bg-light"></td>
                        <td class="bg-light"></td>
                    @endif
                </tr>
                @endfor

                </tbody>
            </table>
        </div>
    </section>
`)