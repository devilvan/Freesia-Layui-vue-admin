import {saveUpdate} from "@/api/common/Icon";
import {SysOssEntity} from "@/types/system/Oss";
import {BaseEntity, BaseVo} from "@/types/Common";

export interface CommonIconVo extends BaseVo {
    name?: string;
    fileId?: string;
    iconPartition?: string;
    remark?: string;
}

export interface CommonIconEntity extends BaseEntity {
    name?: string;
    fileId?: string;
    iconPartition?: string;
    remark?: string;

}

export interface CommonIconSaveUpdateEntity {
    sysOssList?: SysOssEntity[];
    commonIconDto: CommonIconEntity;
}

export interface FindPageCommonIconEntity extends BaseEntity {
    name?: string;
    fileId?: string;
    fileName?: string;
    iconPartition?: string;
    remark?: string;
    url?: string;
}

export interface FindCommonIconEntity extends BaseEntity {
    name?: string;
    fileId?: string;
    fileName?: string;
    iconPartition?: string;
    remark?: string;
    url?: string;
}