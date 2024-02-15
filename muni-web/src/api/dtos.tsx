export type InstitutionDto = {
    id: string;
    name: string;
    logo: string;
};

export type RequisitionDto = {
    id: string;
    link: string;
};

export type AccountDto = {
    externalId: string;
    name: string;
    iban: string;
    currency: string;
    status: 'ACTIVE' | 'EXPIRED';
    institutionName: string;
    institutionLogo: string;
    balance: number;
};

export type TransactionCategoryDto = {
    name: string;
    colorCode: string;
};

export type TransactionDto = {
    externalId: string;
    refFromInstitution: string;
    amount: number;
    bookingDate: Date;
    remittanceInfo: string;
    categories: TransactionCategoryDto[];
};

export type ConditionDto = {
    id?: number;
    separator?: string;
    transactionColumn: string;
    operation: string;
    value: string;
};

export type CategoryDto = {
    id?: number;
    name: string;
    colorCode: string;
    conditions: ConditionDto[];
};

export type CategorySpentAmountDto = {
    categoryName: string;
    categoryColorCode: string;
    spentAmount: number;
}

export type StatsDto = {
    inAmount: number;
    outAmount: number;
    categorySpentAmounts: CategorySpentAmountDto[];
}

export type AccountFullInfoDto = {
    account: AccountDto;
    categories: CategoryDto[];
    stats: StatsDto;
    transactions: TransactionDto[];
};