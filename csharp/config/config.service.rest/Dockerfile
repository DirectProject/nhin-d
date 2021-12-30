#See https://aka.ms/containerfastmode to understand how Visual Studio uses this Dockerfile to build your images for faster debugging.

FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY ["config.service.rest/config.service.rest.csproj", "config.service.rest/"]
RUN dotnet restore "config.service.rest/config.service.rest.csproj"
COPY . .
WORKDIR "/src/config.service.rest"
RUN dotnet build "config.service.rest.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "config.service.rest.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "config.service.rest.dll"]