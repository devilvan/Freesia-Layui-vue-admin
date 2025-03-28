import {saveUpdate} from "@/api/common/Icon";
import {SysOssEntity} from "@/types/system/Oss";

export interface CommonIconVo {
    name: string;
    fileId: string;
    iconPartition: string;
    orderNum: number;
    remark: string;
}

export interface CommonIconEntity {
    name: string;
    fileId: string;
    iconPartition: string;
    orderNum: number;
    remark: string;
}

export interface CommonIconSaveUpdateEntity {
    sysOssList?: SysOssEntity[];
    commonIconDto: CommonIconEntity;
}