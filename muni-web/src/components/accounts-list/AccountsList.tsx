import { Button, Card, CardContent, CardMedia, Chip, Stack, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { AccountDto } from "../../api/dtos";
import { formatToUsd } from "../../utils/currencyFormatUtils";

type Props = {
    accounts: AccountDto[];
};

const AccountsList = ({ accounts }: Props) => {
    const navigate = useNavigate();

    const onSelectAccount = (accountExternalId: string) => {
        navigate(`/accounts/${accountExternalId}`);
    };

    return (
        <div>
            {accounts.map((account, index) =>
                <Card sx={{ marginTop: '32px' }} key={index}>
                    <CardContent
                        sx={{
                            display: 'flex',
                        }}
                    >
                        <CardMedia
                            sx={{
                                height: 90,
                                width: 90,
                                alignSelf: 'center',
                                flexDirection: 'column',
                                marginRight: '24px'
                            }}
                            component="img"
                            image={account.institutionLogo}
                        />

                        <Stack sx={{ flexGrow: 1 }} spacing={0.2}>
                            <Stack direction="row" spacing={1.5}>
                                <Typography variant="h4">
                                    {account.name !== null ? account.name : `${account.institutionName} account`}
                                </Typography>
                                <Chip sx={{ alignSelf: 'center' }} label={account.status} color={account.status === 'ACTIVE' ? 'success' : 'error'} size="small" />
                            </Stack>
                            <Typography variant="subtitle1">{account.iban}</Typography>
                            <Typography variant="subtitle1">{account.institutionName}</Typography>
                            <Typography variant="subtitle1">{`Currency: ${account.currency}`}</Typography>
                            <Typography variant="h6">{`Balance: ${formatToUsd(account.balance)}`}</Typography>
                        </Stack>

                        <Button
                            sx={{
                                maxHeight: '48px',
                                alignSelf: 'center'
                            }}
                            size="large"
                            variant="outlined"
                            onClick={() => onSelectAccount(account.externalId)}
                        >
                            View transactions
                        </Button>
                    </CardContent>
                </Card>
            )}
        </div >
    );
};

export default AccountsList;