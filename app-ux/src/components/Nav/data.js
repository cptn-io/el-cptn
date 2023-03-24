import { IconCloudComputing, IconDatabaseExport, IconDatabaseImport, IconHome, IconTransform } from '@tabler/icons-react';

const navItems = [
    {
        key: "home",
        label: "Home",
        url: "/home",
        Icon: IconHome
    },
    {
        key: "pipelines",
        label: "Data Pipelines",
        url: "/pipelines",
        Icon: IconCloudComputing
    },
    {
        key: "sources",
        label: "Data Sources",
        url: "/sources",
        Icon: IconDatabaseExport
    },
    {
        key: "destinations",
        label: "Data Destinations",
        url: "/destinations",
        Icon: IconDatabaseImport
    },
    {
        key: "transformations",
        label: "Transformations",
        url: "/transformations",
        Icon: IconTransform
    }
];

export { navItems };