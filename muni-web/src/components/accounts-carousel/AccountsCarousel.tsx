import { Card, CardContent, CardMedia, Tab, Tabs, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { AccountDto } from "../../api/dtos";
import { formatToUsd } from "../../utils/currencyFormatUtils";

type Props = {
    accounts: AccountDto[];
};

const AccountsCarousel = ({ accounts }: Props) => {
    const navigate = useNavigate();

    return (
        <Tabs variant="scrollable" scrollButtons value={false}>
            {accounts.map(account => (
                <Tab key={account.externalId} onClick={() => navigate(`/accounts/${account.externalId}`)} label={
                    <Card sx={{ minHeight: '256px', display: 'flex', alignItems: 'center' }}>
                        <CardContent>
                            <CardMedia
                                sx={{ height: 50, width: 50, margin: 'auto', marginBottom: '8px' }}
                                component="img"
                                image={account.institutionLogo}
                            />
                            <Typography variant="h5">{account.name !== null ? account.name : `${account.institutionName} account`}</Typography>
                            <Typography variant="subtitle1">{account.iban}</Typography>
                            <Typography variant="subtitle1">{`Currency: ${account.currency}`}</Typography>
                            <Typography variant="h6">{`Balance: ${formatToUsd(account.balance)}`}</Typography>
                        </CardContent>
                    </Card>
                } />
            ))}
        </Tabs>
    );
};

export default AccountsCarousel;